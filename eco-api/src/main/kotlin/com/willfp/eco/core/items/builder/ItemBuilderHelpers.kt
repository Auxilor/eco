@file:JvmName("ItemBuilderExtensions")

package com.willfp.eco.core.items.builder

import com.willfp.eco.core.items.TestableItem
import org.bukkit.inventory.ItemStack

/**
 * Modify the receiver's [TestableItem.getItem] with a builder.
 *
 * @param builder The builder.
 * @return The modified item.
 */
fun TestableItem.modify(builder: ItemBuilder.() -> Unit): ItemStack =
    this.item.modify(builder)

/**
 * Modify the receiver item with a builder.
 *
 * @param builder The builder.
 * @return The modified item.
 * @see ItemStackBuilder
 */
fun ItemStack.modify(builder: ItemBuilder.() -> Unit): ItemStack =
    ItemStackBuilder(this).apply(builder).build()
