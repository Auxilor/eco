package com.willfp.eco.internal.spigot.proxy.v1_21_8

import com.willfp.eco.internal.spigot.proxies.DisplayRecordsProxy
import com.willfp.eco.internal.spigot.proxy.common.item.DisplayRecordCodec
import net.minecraft.world.item.ItemStack as NMSItemStack
import org.bukkit.inventory.ItemStack

class DisplayRecords : DisplayRecordsProxy {
    override fun snapshot(itemStack: ItemStack): Any =
        DisplayRecordCodec.snapshot(itemStack)

    override fun record(itemStack: ItemStack, snapshot: Any, displayLines: IntArray, recordLore: Boolean) =
        DisplayRecordCodec.record(itemStack, snapshot as NMSItemStack, displayLines, recordLore)

    override fun restore(itemStack: ItemStack): Boolean =
        DisplayRecordCodec.restore(itemStack)
}
