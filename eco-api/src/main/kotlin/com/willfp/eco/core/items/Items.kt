@file:JvmName("ItemsExtensions")

package com.willfp.eco.core.items

import org.bukkit.inventory.ItemStack

/**
 * @see Items.toLookupString
 */
fun ItemStack?.toLookupString(): String =
    Items.toLookupString(this)
