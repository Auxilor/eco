package com.willfp.eco.internal.spigot.anvil

import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.TypedKey
import org.bukkit.inventory.ItemStack

/**
 * Vanilla-style "unit repair" rules (e.g. iron ingot repairs iron tools, breeze rod repairs a mace).
 *
 * Read from the item's own `minecraft:repairable` data component rather than a table maintained by
 * eco, so every item the server accepts as a repair unit works - including items added in newer
 * Minecraft versions, and items whose repair materials are changed by a datapack.
 */
object AnvilRepair {
    /** Whether [other] (the right item) can unit-repair [this] (the left item). */
    @Suppress("UnstableApiUsage")
    fun ItemStack.canUnitRepairWith(other: ItemStack): Boolean {
        val repairable = getData(DataComponentTypes.REPAIRABLE) ?: return false
        return repairable.types().contains(TypedKey.create(RegistryKey.ITEM, other.type.key))
    }
}
