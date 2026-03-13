package com.willfp.eco.internal.spigot.proxy.v1_21_11

import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.serialization.Dynamic
import com.willfp.eco.core.items.TestableItem
import com.willfp.eco.core.recipe.parts.EmptyTestableItem
import com.willfp.eco.internal.spigot.proxies.SNBTConverterProxy
import net.minecraft.SharedConstants
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.SnbtPrinterTagVisitor
import net.minecraft.nbt.TagParser
import net.minecraft.server.MinecraftServer
import net.minecraft.util.datafix.fixes.References
import org.bukkit.craftbukkit.inventory.CraftItemStack
import org.bukkit.craftbukkit.util.CraftMagicNumbers
import org.bukkit.inventory.ItemStack


class SNBTConverter : SNBTConverterProxy {
    private fun parseItemSNBT(snbt: String): CompoundTag? {
        val nbt = runCatching { TagParser.parseCompoundFully(snbt) }.getOrNull() ?: return null
        val dataVersion = if (nbt.contains("DataVersion")) {
            nbt.getInt("DataVersion").get()
        } else null

        // If the data version is the same as the server's data version, we don't need to fix it

        if (dataVersion == CraftMagicNumbers.INSTANCE.dataVersion) {
            return nbt
        }

        return MinecraftServer.getServer().fixerUpper.update(
            References.ITEM_STACK,
            Dynamic(NbtOps.INSTANCE, nbt),
            dataVersion ?: 3700, // 3700 is the 1.20.4 data version
            CraftMagicNumbers.INSTANCE.dataVersion
        ).value as CompoundTag
    }

    override fun fromSNBT(snbt: String): ItemStack? {
        try {
            val parsed = TagParser.parseCompoundFully(snbt)
            val dataVersion = parsed.getIntOr("DataVersion", 0)
            val converted = MinecraftServer.getServer().fixerUpper.update(
                References.ITEM_STACK,
                Dynamic(NbtOps.INSTANCE, parsed),
                dataVersion,
                SharedConstants.getCurrentVersion().dataVersion().version()
            ).getValue() as CompoundTag?
            val minecraftStack = net.minecraft.world.item.ItemStack.CODEC.parse(
                MinecraftServer.getServer().registryAccess().createSerializationContext(NbtOps.INSTANCE),
                converted
            ).getOrThrow()
            return CraftItemStack.asCraftMirror(minecraftStack)
        } catch (_: CommandSyntaxException) {
            return null
        }
    }

    override fun toSNBT(itemStack: ItemStack): String {
        val compoundTag = net.minecraft.world.item.ItemStack.CODEC.encodeStart(
            MinecraftServer.getServer().registryAccess().createSerializationContext(NbtOps.INSTANCE),
            CraftItemStack.asNMSCopy(itemStack)
        ).getOrThrow() as CompoundTag
        compoundTag.putInt("DataVersion", SharedConstants.getCurrentVersion().dataVersion().version())
        return SnbtPrinterTagVisitor().visit(compoundTag)
    }

    override fun makeSNBTTestable(snbt: String): TestableItem {
        try {
            val tag = parseItemSNBT(snbt) ?: return EmptyTestableItem()
            tag.remove("Count")
            val minecraftStack = net.minecraft.world.item.ItemStack.CODEC.parse(
                MinecraftServer.getServer().registryAccess().createSerializationContext(NbtOps.INSTANCE),
                tag
            ).getOrThrow()
            return SNBTTestableItem(CraftItemStack.asBukkitCopy(minecraftStack), tag)
        } catch (_: CommandSyntaxException) {
            return EmptyTestableItem()
        }

    }

    class SNBTTestableItem(
        private val item: ItemStack,
        private val tag: CompoundTag
    ) : TestableItem {
        override fun matches(itemStack: ItemStack?): Boolean {
            if (itemStack == null) {
                return false
            }

            val nmsTag = net.minecraft.world.item.ItemStack.CODEC.encodeStart(
                MinecraftServer.getServer().registryAccess().createSerializationContext(NbtOps.INSTANCE),
                CraftItemStack.asNMSCopy(itemStack)
            ).getOrThrow() as CompoundTag
            nmsTag.remove("Count")
            return tag.copy().merge(nmsTag) == nmsTag && itemStack.type == item.type
        }

        override fun getItem(): ItemStack = item
    }
}
