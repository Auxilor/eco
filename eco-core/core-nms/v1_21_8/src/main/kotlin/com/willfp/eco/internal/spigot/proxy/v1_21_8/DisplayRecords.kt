package com.willfp.eco.internal.spigot.proxy.v1_21_8

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.eco.core.Eco
import com.willfp.eco.core.display.Display
import com.willfp.eco.internal.spigot.proxies.DisplayRecordsProxy
import com.willfp.eco.internal.spigot.proxy.common.asBukkitStack
import com.willfp.eco.internal.spigot.proxy.common.asNMSStack
import com.willfp.eco.internal.spigot.proxy.common.item.unstyled
import com.willfp.eco.internal.spigot.proxy.common.mergeIfNeeded
import com.willfp.eco.internal.spigot.proxy.common.toAdventure
import com.willfp.eco.internal.spigot.proxy.common.toNMS
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.BitSet
import java.util.Optional
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import net.kyori.adventure.text.Component
import net.minecraft.core.component.DataComponentMap
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtIo
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import net.minecraft.resources.RegistryOps
import net.minecraft.world.item.ItemStack as NMSItemStack
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.item.component.ItemLore
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.CraftServer
import org.bukkit.craftbukkit.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack

private const val RECORD_KEY = "eco_display"
private const val DATA_KEY = "data"
private const val SIGNATURE_KEY = "signature"
private const val SIGNATURE_ALGORITHM = "HmacSHA256"
private const val CIPHER_ALGORITHM = "AES/CTR/NoPadding"
private const val CIPHER_IV_LENGTH = 16
private const val KEY_LENGTH = 32
private const val KEY_FILE_NAME = "display-keys.dat"
private const val MAX_RECORD_SIZE = 8192
private const val SEALED_CACHE_SIZE = 10_000L
private const val LINES_KEY = "lines"
private const val RESTORE_KEY = "restore"
private const val PLAIN_LORE_KEY = "plain_lore"

private class RecordKeys(
    val signing: SecretKeySpec,
    val cipher: SecretKeySpec
)

private enum class KeptLore {
    STYLED,
    PLAIN
}

class DisplayRecords(
    private val keyFile: File = File(Eco.get().ecoPlugin.dataFolder, KEY_FILE_NAME),
    registryOps: () -> RegistryOps<Tag> = {
        (Bukkit.getServer() as CraftServer).server.registryAccess().createSerializationContext(NbtOps.INSTANCE)
    }
) : DisplayRecordsProxy {
    private val registryOps by lazy(registryOps)

    private val keys by lazy { loadKeys() }

    private val signers = ThreadLocal.withInitial {
        Mac.getInstance(SIGNATURE_ALGORITHM).apply { init(keys.signing) }
    }

    private val ciphers = ThreadLocal.withInitial {
        Cipher.getInstance(CIPHER_ALGORITHM)
    }

    private val sealedRecords: Cache<CompoundTag, Optional<CompoundTag>> = Caffeine.newBuilder()
        .maximumSize(SEALED_CACHE_SIZE)
        .build()

    override fun mirror(itemStack: ItemStack): ItemStack =
        itemStack as? CraftItemStack ?: itemStack.asNMSStack().asBukkitStack()

    override fun getLoreState(itemStack: ItemStack): Any? =
        itemStack.asNMSStack().get(DataComponents.LORE)

    override fun setLore(itemStack: ItemStack, lore: List<Component>) {
        val handle = itemStack.asNMSStack()
        handle.set(
            DataComponents.LORE,
            ItemLore(lore.map { line -> (if (Display.isDisplayLine(line)) line.unstyled() else line).toNMS() })
        )
        itemStack.mergeIfNeeded(handle)
    }

    override fun record(itemStack: ItemStack, snapshot: ItemStack, displayLines: IntArray) {
        val handle = itemStack.asNMSStack()
        val snapshotHandle = snapshot.asNMSStack()

        if (handle.item != snapshotHandle.item || handle.count != snapshotHandle.count) {
            return
        }

        val beforePatch = snapshotHandle.componentsPatch
        val afterPatch = handle.componentsPatch

        if (displayLines.isEmpty() && beforePatch == afterPatch) {
            return
        }

        val before = snapshotHandle.components
        val after = handle.components
        val keptLore = if (displayLines.isEmpty()) null else keptLore(before, after, displayLines)
        val restore = DataComponentPatch.builder()

        for (type in beforePatch.changedTypes() + afterPatch.changedTypes()) {
            if (type == DataComponents.LORE && keptLore != null) {
                continue
            }

            if (before.get(type) == after.get(type)) {
                continue
            }

            restore.restoreTo(type, before)
        }

        val restorePatch = restore.build()

        if (displayLines.isEmpty() && restorePatch.isEmpty) {
            return
        }

        val encodedRestore = DataComponentPatch.CODEC.encodeStart(registryOps, restorePatch).result().orElse(null)
            ?: return

        val sealed = seal(CompoundTag().apply {
            putIntArray(LINES_KEY, displayLines)
            put(RESTORE_KEY, encodedRestore)

            if (keptLore == KeptLore.PLAIN) {
                putBoolean(PLAIN_LORE_KEY, true)
            }
        }) ?: return

        handle.set(
            DataComponents.CUSTOM_DATA,
            handle.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).update {
                it.put(RECORD_KEY, sealed)
            }
        )

        itemStack.mergeIfNeeded(handle)
    }

    override fun restore(itemStack: ItemStack): Boolean {
        var restored = false

        while (restoreOnce(itemStack)) {
            restored = true
        }

        return restored
    }

    private fun restoreOnce(itemStack: ItemStack): Boolean {
        val handle = itemStack.asNMSStack()
        val customData = handle.get(DataComponents.CUSTOM_DATA)
            ?.takeIf { it.contains(RECORD_KEY) }
            ?.copyTag()
            ?: return false
        val record = customData.getCompound(RECORD_KEY).flatMap { open(it) }.orElse(null)
        // CompoundTag#remove returns a Tag from 26.1, so calling it can't link on every version.
        customData.keySet().remove(RECORD_KEY)

        if (customData.isEmpty) {
            handle.remove(DataComponents.CUSTOM_DATA)
        } else {
            handle.set(DataComponents.CUSTOM_DATA, CustomData.of(customData))
        }

        val restored = record != null && restoreFrom(handle, record)
        itemStack.mergeIfNeeded(handle)

        return restored
    }

    private fun restoreFrom(handle: NMSItemStack, record: CompoundTag): Boolean {
        val displayLines = record.getIntArray(LINES_KEY).orElse(IntArray(0))
        val restore = record.get(RESTORE_KEY)
            ?.let { DataComponentPatch.CODEC.parse(registryOps, it).result().orElse(null) }
            ?: return false
        val lore = handle.getOrDefault(DataComponents.LORE, ItemLore.EMPTY).lines

        if (!displayLines.isStrictlyIncreasingWithin(lore.size)
            || displayLines.any { !Display.isDisplayLine(lore[it].toAdventure()) }
        ) {
            return false
        }

        if (displayLines.isNotEmpty()) {
            val displayed = displayLines.toBitSet()
            val kept = lore.filterIndexed { index, _ -> !displayed[index] }
            handle.set(
                DataComponents.LORE,
                if (record.getBooleanOr(PLAIN_LORE_KEY, false)) ItemLore(kept, kept) else ItemLore(kept)
            )
        }

        handle.applyComponents(restore)

        return true
    }

    private fun seal(record: CompoundTag): CompoundTag? =
        sealedRecords.get(record) { encrypt(it) }.map { it.copy() }.orElse(null)

    private fun encrypt(record: CompoundTag): Optional<CompoundTag> {
        val data = ByteArrayOutputStream().also { NbtIo.write(record, DataOutputStream(it)) }.toByteArray()

        if (data.size > MAX_RECORD_SIZE) {
            return Optional.empty()
        }

        val signature = signers.get().doFinal(data)

        return Optional.of(CompoundTag().apply {
            putByteArray(DATA_KEY, crypt(signature, data))
            putByteArray(SIGNATURE_KEY, signature)
        })
    }

    private fun open(sealed: CompoundTag): Optional<CompoundTag> {
        val encrypted = sealed.getByteArray(DATA_KEY).orElse(null) ?: return Optional.empty()
        val signature = sealed.getByteArray(SIGNATURE_KEY).orElse(null) ?: return Optional.empty()

        if (signature.size < CIPHER_IV_LENGTH || encrypted.size > MAX_RECORD_SIZE) {
            return Optional.empty()
        }

        val data = crypt(signature, encrypted)

        if (!MessageDigest.isEqual(signature, signers.get().doFinal(data))) {
            return Optional.empty()
        }

        return runCatching { NbtIo.read(DataInputStream(ByteArrayInputStream(data))) }
            .map { Optional.of(it) }
            .getOrDefault(Optional.empty())
    }

    private fun crypt(signature: ByteArray, data: ByteArray): ByteArray =
        ciphers.get().run {
            init(Cipher.ENCRYPT_MODE, keys.cipher, IvParameterSpec(signature, 0, CIPHER_IV_LENGTH))
            doFinal(data)
        }

    private fun loadKeys(): RecordKeys {
        val bytes = keyFile.takeIf { it.isFile && it.length() == KEY_LENGTH * 2L }?.readBytes()
            ?: ByteArray(KEY_LENGTH * 2).also {
                SecureRandom().nextBytes(it)
                keyFile.parentFile?.mkdirs()
                keyFile.writeBytes(it)
            }

        return RecordKeys(
            SecretKeySpec(bytes, 0, KEY_LENGTH, SIGNATURE_ALGORITHM),
            SecretKeySpec(bytes, KEY_LENGTH, KEY_LENGTH, "AES")
        )
    }

    private fun IntArray.isStrictlyIncreasingWithin(size: Int): Boolean =
        this.size <= size && this.indices.all { index ->
            this[index] in 0 until size && (index == 0 || this[index] > this[index - 1])
        }

    private fun IntArray.toBitSet(): BitSet =
        BitSet().also { bits -> this.forEach { bits.set(it) } }

    private fun keptLore(before: DataComponentMap, after: DataComponentMap, displayLines: IntArray): KeptLore? {
        val beforeLore = before.get(DataComponents.LORE) ?: ItemLore.EMPTY
        val afterLines = (after.get(DataComponents.LORE) ?: ItemLore.EMPTY).lines

        val displayed = displayLines.toBitSet()

        if (afterLines.filterIndexed { index, _ -> !displayed[index] } != beforeLore.lines) {
            return null
        }

        return when (beforeLore) {
            ItemLore(beforeLore.lines) -> KeptLore.STYLED
            ItemLore(beforeLore.lines, beforeLore.lines) -> KeptLore.PLAIN
            else -> null
        }
    }

    private fun DataComponentPatch.changedTypes(): Set<DataComponentType<*>> =
        split().let { it.added().keySet() + it.removed() }

    @Suppress("UNCHECKED_CAST")
    private fun DataComponentPatch.Builder.restoreTo(
        type: DataComponentType<*>,
        before: DataComponentMap
    ) {
        val typed = type as DataComponentType<Any>
        val value = before.get(typed)

        if (value == null) {
            remove(typed)
        } else {
            set(typed, value)
        }
    }
}
