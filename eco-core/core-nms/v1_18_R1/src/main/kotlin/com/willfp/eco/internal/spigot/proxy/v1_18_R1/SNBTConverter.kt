package com.willfp.eco.internal.spigot.proxy.v1_18_R1

import com.willfp.eco.internal.spigot.proxy.SNBTConverterProxy
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.SnbtPrinterTagVisitor
import net.minecraft.nbt.TagParser
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack

class SNBTConverter : SNBTConverterProxy {
    override fun fromSNBT(snbt: String): ItemStack? {
        val nbt = runCatching { TagParser.parseTag(snbt) }.getOrNull() ?: return null
        val nms = net.minecraft.world.item.ItemStack.of(nbt)
        return CraftItemStack.asBukkitCopy(nms)
    }

    override fun toSNBT(itemStack: ItemStack): String {
        val nms = CraftItemStack.asNMSCopy(itemStack)
        return SnbtPrinterTagVisitor().visit(nms.save(CompoundTag()))
    }
}