package com.willfp.eco.internal.spigot.proxies

import org.bukkit.inventory.ItemStack

interface AnvilRepairProxy {
    /** Whether [repairMaterial] can vanilla unit-repair [item] (e.g. a breeze rod repairing a mace). */
    fun canUnitRepair(item: ItemStack, repairMaterial: ItemStack): Boolean
}
