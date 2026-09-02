package com.willfp.eco.internal.spigot.proxies

import org.bukkit.inventory.ItemStack

/** The [damage] left on an item after a unit repair consuming [units] of the repair material. */
data class UnitRepair(val damage: Int, val units: Int)

interface AnvilRepairProxy {
    /**
     * Vanilla unit repair (e.g. a breeze rod repairing a mace), or null if the material can't
     * repair the item, the item isn't damageable, or there's no damage left to repair.
     */
    fun unitRepair(item: ItemStack, repairMaterial: ItemStack): UnitRepair?

    /**
     * The damage [item] is left with after merging [sacrifice]'s remaining durability into it,
     * or null if the item isn't damageable or the merge wouldn't repair it.
     */
    fun combineRepair(item: ItemStack, sacrifice: ItemStack): Int?
}
