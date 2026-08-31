package com.willfp.eco.internal.spigot.proxy.v1_21_8

import com.willfp.eco.internal.spigot.proxies.AnvilRepairProxy
import com.willfp.eco.internal.spigot.proxy.common.item.canUnitRepair as nmsCanUnitRepair
import org.bukkit.inventory.ItemStack

class AnvilRepair : AnvilRepairProxy {
    override fun canUnitRepair(item: ItemStack, repairMaterial: ItemStack): Boolean =
        nmsCanUnitRepair(item, repairMaterial)
}
