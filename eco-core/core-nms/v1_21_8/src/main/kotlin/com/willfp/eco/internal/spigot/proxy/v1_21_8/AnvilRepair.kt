package com.willfp.eco.internal.spigot.proxy.v1_21_8

import com.willfp.eco.internal.spigot.proxies.AnvilRepairProxy
import com.willfp.eco.internal.spigot.proxies.UnitRepair
import com.willfp.eco.internal.spigot.proxy.common.item.Repairing
import org.bukkit.inventory.ItemStack

class AnvilRepair : AnvilRepairProxy {
    override fun unitRepair(item: ItemStack, repairMaterial: ItemStack): UnitRepair? =
        Repairing.unitRepair(item, repairMaterial)

    override fun combineRepair(item: ItemStack, sacrifice: ItemStack): Int? =
        Repairing.combineRepair(item, sacrifice)
}
