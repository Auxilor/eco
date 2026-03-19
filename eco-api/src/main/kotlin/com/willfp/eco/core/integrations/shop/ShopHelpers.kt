@file:JvmName("ShopExtensions")

package com.willfp.eco.core.integrations.shop

import com.willfp.eco.core.price.Price
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack


/** @see ShopManager.getUnitValue */
fun ItemStack.getUnitValue(player: Player): Price =
    ShopManager.getUnitValue(this, player)

/** @see ShopManager.isSellable */
fun ItemStack?.isSellable(player: Player): Boolean =
    ShopManager.isSellable(this, player)
