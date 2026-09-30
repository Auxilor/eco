package com.willfp.eco.internal.spigot.proxy.v1_21_8

import com.willfp.eco.core.display.Display
import com.willfp.eco.internal.spigot.proxies.DisplayRecordsProxy
import com.willfp.eco.internal.spigot.proxy.common.asNMSStack
import com.willfp.eco.internal.spigot.proxy.common.item.unstyled
import com.willfp.eco.internal.spigot.proxy.common.mergeIfNeeded
import com.willfp.eco.internal.spigot.proxy.common.toAdventure
import com.willfp.eco.internal.spigot.proxy.common.toNMS
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.BitSet
import java.util.Optional
import javax.crypto.Mac
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
import org.bukkit.inventory.ItemStack

private const val RECORD_KEY = "eco_display"
private const val DATA_KEY = "data"
private const val SIGNATURE_KEY = "signature"
private const val SIGNATURE_ALGORITHM = "HmacSHA256"
private const val LINES_KEY = "lines"
private const val RESTORE_KEY = "restore"
private const val PLAIN_LORE_KEY = "plain_lore"

private val registryOps: RegistryOps<Tag> by lazy {
    (Bukkit.getServer() as CraftServer).server.registryAccess().createSerializationContext(NbtOps.INSTANCE)
}

private val signingKey = SecretKeySpec(ByteArray(32).also { SecureRandom().nextBytes(it) }, SIGNATURE_ALGORITHM)

private val signers = ThreadLocal.withInitial {
    Mac.getInstance(SIGNATURE_ALGORITHM).apply { init(signingKey) }
}

private enum class KeptLore {
    STYLED,
    PLAIN
}

class DisplayRecords : DisplayRecordsProxy {
    override fun getLore(itemStack: ItemStack): List<Component> =
        itemStack.asNMSStack().getOrDefault(DataComponents.LORE, ItemLore.EMPTY).lines.map { it.toAdventure() }

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

        val before = snapshotHandle.components
        val after = handle.components
        val keptLore = if (displayLines.isEmpty()) null else keptLore(before, after, displayLines)
        val restore = DataComponentPatch.builder()

        for (type in snapshotHandle.componentsPatch.changedTypes() + handle.componentsPatch.changedTypes()) {
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

        handle.set(
            DataComponents.CUSTOM_DATA,
            handle.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).update {
                it.put(RECORD_KEY, sign(CompoundTag().apply {
                    putIntArray(LINES_KEY, displayLines)
                    put(RESTORE_KEY, DataComponentPatch.CODEC.encodeStart(registryOps, restorePatch).getOrThrow())

                    if (keptLore == KeptLore.PLAIN) {
                        putBoolean(PLAIN_LORE_KEY, true)
                    }
                }))
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
        val record = customData.getCompound(RECORD_KEY).flatMap { verify(it) }.orElse(null)
        val withoutRecord = CustomData.of(customData.without(RECORD_KEY))

        if (withoutRecord.isEmpty) {
            handle.remove(DataComponents.CUSTOM_DATA)
        } else {
            handle.set(DataComponents.CUSTOM_DATA, withoutRecord)
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

    private fun sign(record: CompoundTag): CompoundTag {
        val data = ByteArrayOutputStream().also { NbtIo.write(record, DataOutputStream(it)) }.toByteArray()

        return CompoundTag().apply {
            putByteArray(DATA_KEY, data)
            putByteArray(SIGNATURE_KEY, signers.get().doFinal(data))
        }
    }

    private fun verify(signed: CompoundTag): Optional<CompoundTag> {
        val data = signed.getByteArray(DATA_KEY).orElse(null) ?: return Optional.empty()
        val signature = signed.getByteArray(SIGNATURE_KEY).orElse(null) ?: return Optional.empty()

        if (!MessageDigest.isEqual(signature, signers.get().doFinal(data))) {
            return Optional.empty()
        }

        return runCatching { NbtIo.read(DataInputStream(ByteArrayInputStream(data))) }
            .map { Optional.of(it) }
            .getOrDefault(Optional.empty())
    }

    private fun IntArray.isStrictlyIncreasingWithin(size: Int): Boolean =
        this.size <= size && this.indices.all { index ->
            this[index] in 0 until size && (index == 0 || this[index] > this[index - 1])
        }

    private fun IntArray.toBitSet(): BitSet =
        BitSet().also { bits -> this.forEach { bits.set(it) } }

    private fun CompoundTag.without(key: String): CompoundTag =
        CompoundTag().also { copy ->
            for (existing in this.keySet()) {
                if (existing != key) {
                    copy.put(existing, this.get(existing)!!)
                }
            }
        }

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
