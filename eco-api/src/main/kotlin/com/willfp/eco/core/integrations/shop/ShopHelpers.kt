@file:JvmName("ShopExtensions")

package com.willfp.eco.core.integrations.shop

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/** @see ShopManager.getItemPrice **/
val ItemStack.price: Double
    get() = ShopManager.getItemPrice(this)

/** @see ShopManager.getItemPrice **/
fun ItemStack.getPrice(player: Player): Double =
    ShopManager.getItemPrice(this, player)
