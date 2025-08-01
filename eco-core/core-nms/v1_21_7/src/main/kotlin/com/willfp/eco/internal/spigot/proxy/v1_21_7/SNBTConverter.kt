package com.willfp.eco.internal.spigot.proxy.v1_21_7

import com.mojang.serialization.Dynamic
import com.willfp.eco.core.items.TestableItem
import com.willfp.eco.core.recipe.parts.EmptyTestableItem
import com.willfp.eco.internal.spigot.proxy.SNBTConverterProxy
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.SnbtPrinterTagVisitor
import net.minecraft.nbt.TagParser
import net.minecraft.server.MinecraftServer
import net.minecraft.util.datafix.fixes.References
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.CraftServer
import org.bukkit.craftbukkit.inventory.CraftItemStack
import org.bukkit.craftbukkit.util.CraftMagicNumbers
import org.bukkit.inventory.ItemStack

private val registryAccess = (Bukkit.getServer() as CraftServer).server.registryAccess()

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


    private fun nmsToNbt(nms: net.minecraft.world.item.ItemStack): CompoundTag {
        return net.minecraft.world.item.ItemStack.CODEC
            .encodeStart(registryAccess.createSerializationContext(NbtOps.INSTANCE), nms)
            .result()
            .get() as CompoundTag
    }

    private fun nbtToNms(tag: CompoundTag): net.minecraft.world.item.ItemStack? {
        return net.minecraft.world.item.ItemStack.CODEC
            .parse(registryAccess.createSerializationContext(NbtOps.INSTANCE), tag)
            .result()
            .orElse(null)
    }

    override fun fromSNBT(snbt: String): ItemStack? {
        val tag = parseItemSNBT(snbt) ?: return null
        val nms = nbtToNms(tag) ?: return null
        return CraftItemStack.asBukkitCopy(nms)
    }

    override fun toSNBT(itemStack: ItemStack): String {
        val nms = CraftItemStack.asNMSCopy(itemStack)
        val tag = nmsToNbt(nms)
        tag.putInt("DataVersion", CraftMagicNumbers.INSTANCE.dataVersion)
        return SnbtPrinterTagVisitor().visit(tag)
    }

    override fun makeSNBTTestable(snbt: String): TestableItem {
        val tag = parseItemSNBT(snbt) ?: return EmptyTestableItem()
        val nms = nbtToNms(tag)

        tag.remove("Count")
        return SNBTTestableItem(CraftItemStack.asBukkitCopy(nms), tag)
    }

    inner class SNBTTestableItem(
        private val item: ItemStack,
        private val tag: CompoundTag
    ) : TestableItem {
        override fun matches(itemStack: ItemStack?): Boolean {
            if (itemStack == null) {
                return false
            }

            val nms = CraftItemStack.asNMSCopy(itemStack)
            val nmsTag = nmsToNbt(nms)
            nmsTag.remove("Count")
            return tag.copy().merge(nmsTag) == nmsTag && itemStack.type == item.type
        }

        override fun getItem(): ItemStack = item
    }
}
