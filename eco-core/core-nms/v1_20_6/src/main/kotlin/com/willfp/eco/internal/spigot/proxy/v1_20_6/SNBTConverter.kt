package com.willfp.eco.internal.spigot.proxy.v1_20_6

import com.willfp.eco.core.items.TestableItem
import com.willfp.eco.core.recipe.parts.EmptyTestableItem
import com.willfp.eco.internal.spigot.proxy.SNBTConverterProxy
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.SnbtPrinterTagVisitor
import net.minecraft.nbt.TagParser
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.CraftServer
import org.bukkit.craftbukkit.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack

private val holderLookupProvider = (Bukkit.getServer() as CraftServer).server.registryAccess()

class SNBTConverter : SNBTConverterProxy {
    override fun fromSNBT(snbt: String): ItemStack? {
        val nbt = runCatching { TagParser.parseTag(snbt) }.getOrNull() ?: return null
        val nms = net.minecraft.world.item.ItemStack.parse(holderLookupProvider, nbt).orElse(null) ?: return null
        return CraftItemStack.asBukkitCopy(nms)
    }

    override fun toSNBT(itemStack: ItemStack): String {
        val nms = CraftItemStack.asNMSCopy(itemStack)
        return SnbtPrinterTagVisitor().visit(nms.save(holderLookupProvider))
    }

    override fun makeSNBTTestable(snbt: String): TestableItem {
        val nbt = runCatching { TagParser.parseTag(snbt) }.getOrNull() ?: return EmptyTestableItem()
        val nms = net.minecraft.world.item.ItemStack.parse(holderLookupProvider, nbt).orElse(null)
            ?: return EmptyTestableItem()

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
            val nmsTag = nms.save(holderLookupProvider) as CompoundTag
            nmsTag.remove("Count")
            return tag.copy().merge(nmsTag) == nmsTag && itemStack.type == item.type
        }

        override fun getItem(): ItemStack = item
    }
}
