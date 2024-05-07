package com.willfp.eco.internal.spigot.proxy.v1_19_R3

import com.willfp.eco.core.items.TestableItem
import com.willfp.eco.core.recipe.parts.EmptyTestableItem
import com.willfp.eco.internal.spigot.proxy.SNBTConverterProxy
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.SnbtPrinterTagVisitor
import net.minecraft.nbt.TagParser
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack
import org.bukkit.craftbukkit.v1_19_R3.util.CraftMagicNumbers
import org.bukkit.inventory.ItemStack

class SNBTConverter : SNBTConverterProxy {
    override fun fromSNBT(snbt: String): ItemStack? {
        val nbt = runCatching { TagParser.parseTag(snbt) }.getOrNull() ?: return null
        val nms = net.minecraft.world.item.ItemStack.of(nbt)
        return CraftItemStack.asBukkitCopy(nms)
    }

    override fun toSNBT(itemStack: ItemStack): String {
        val nms = CraftItemStack.asNMSCopy(itemStack)
        val tag = nms.save(CompoundTag())
        tag.putInt("DataVersion", CraftMagicNumbers.INSTANCE.dataVersion)
        return SnbtPrinterTagVisitor().visit(tag)
    }

    override fun makeSNBTTestable(snbt: String): TestableItem {
        val nbt = runCatching { TagParser.parseTag(snbt) }.getOrNull() ?: return EmptyTestableItem()
        val nms = net.minecraft.world.item.ItemStack.of(nbt)
        if (nms == net.minecraft.world.item.ItemStack.EMPTY) {
            return EmptyTestableItem()
        }

        nbt.remove("Count")
        return SNBTTestableItem(CraftItemStack.asBukkitCopy(nms), nbt)
    }

    class SNBTTestableItem(
        private val item: ItemStack,
        private val tag: CompoundTag
    ) : TestableItem {
        override fun matches(itemStack: ItemStack?): Boolean {
            if (itemStack == null) {
                return false
            }

            val nms = CraftItemStack.asNMSCopy(itemStack)
            val nmsTag = nms.save(CompoundTag())
            nmsTag.remove("Count")
            return tag.copy().merge(nmsTag) == nmsTag && itemStack.type == item.type
        }

        override fun getItem(): ItemStack = item
    }
}
