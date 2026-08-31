package com.willfp.eco.internal.spigot.proxy.common.item

import com.willfp.eco.internal.spigot.proxy.common.asNMSStack
import org.bukkit.inventory.ItemStack

/**
 * Whether [repairMaterial] can vanilla unit-repair [item], as decided by the item's own
 * `minecraft:repairable` component - so eco doesn't have to maintain a material table that
 * silently goes stale whenever Minecraft adds an item, or a datapack changes one.
 */
fun canUnitRepair(item: ItemStack, repairMaterial: ItemStack): Boolean =
    item.asNMSStack().isValidRepairItem(repairMaterial.asNMSStack())
