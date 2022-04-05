@file:JvmName("ItemsExtensions")

package com.willfp.eco.core.items

import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

/**
 * @see Items.toLookupString
 */
fun ItemStack?.toLookupString(): String =
    Items.toLookupString(this)

/**
 * @see Items.mergeFrom
 */
fun ItemStack.mergeFrom(other: ItemStack): ItemStack =
    Items.mergeFrom(other, this)

/**
 * @see Items.mergeFrom
 */
fun ItemMeta.mergeFrom(other: ItemMeta): ItemMeta =
    Items.mergeFrom(other, this)
