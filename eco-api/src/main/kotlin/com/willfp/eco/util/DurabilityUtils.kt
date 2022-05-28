@file:JvmName("DurabilityUtilsExtensions")

package com.willfp.eco.util

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/** @see DurabilityUtils.damageItem */
fun ItemStack.damage(damage: Int) =
    DurabilityUtils.damageItem(this, damage)

/** @see DurabilityUtils.damageItem */
fun ItemStack.damage(damage: Int, player: Player) =
    DurabilityUtils.damageItem(player, this, damage)
