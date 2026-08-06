@file:JvmName("DurabilityUtilsExtensions")

package com.willfp.eco.util

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * Damage this item by a number of durability points, with no player involved.
 *
 * Does nothing if the item has no meta, is unbreakable, has meta that is not damageable,
 * or is a carved pumpkin or player head. If the accumulated damage reaches the material's
 * maximum durability the item's type is set to air.
 *
 * @param damage The number of durability points to remove.
 * @see DurabilityUtils.damageItem
 */
fun ItemStack.damage(damage: Int) =
    DurabilityUtils.damageItem(this, damage)

/**
 * Damage this item by a number of durability points as though the given player used it.
 *
 * Fires a `PlayerItemDamageEvent` first, and respects both its cancellation and any change
 * it makes to the damage amount. Does nothing if the item has no meta, is unbreakable, has
 * meta that is not damageable, or is a carved pumpkin or player head. If the accumulated
 * damage reaches the material's maximum durability, a `PlayerItemBreakEvent` is fired, the
 * item's type is set to air, and the item break sound is played to the player.
 *
 * @param damage The number of durability points to remove.
 * @param player The player to attribute the damage to.
 * @see DurabilityUtils.damageItem
 */
fun ItemStack.damage(damage: Int, player: Player) =
    DurabilityUtils.damageItem(player, this, damage)
