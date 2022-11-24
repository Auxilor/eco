@file:JvmName("ShopExtensions")

package com.willfp.eco.core.integrations.shop

import com.willfp.eco.core.price.Price
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/** @see ShopManager.getItemPrice * */
@Deprecated(
    "Prices depend on players, this will always return 0.",
    level = DeprecationLevel.ERROR,
    replaceWith = ReplaceWith("this.getValue(player)")
)
val ItemStack.price: Double
    get() = 0.0

/** @see ShopManager.getItemPrice * */
@Deprecated(
    "Use the price system instead, prices may not be currencies.",
    ReplaceWith("this.getValue(player)"),
)
fun ItemStack.getPrice(player: Player): Double =
    this.getValue(player).getValue(player)

/** @see ShopManager.getValue */
fun ItemStack.getValue(player: Player): Price =
    ShopManager.getValue(this, player)

/** @see ShopManager.isSellable */
fun ItemStack?.isSellable(player: Player): Boolean =
    ShopManager.isSellable(this, player)
