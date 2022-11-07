@file:JvmName("ItemBuilderExtensions")

package com.willfp.eco.core.items.builder

import com.willfp.eco.core.items.TestableItem
import org.bukkit.inventory.ItemStack

/** Modify an item with a builder. */
fun TestableItem.modify(builder: ItemBuilder.() -> Unit): ItemStack =
    this.item.modify(builder)

/** Modify an item with a builder. */
fun ItemStack.modify(builder: ItemBuilder.() -> Unit): ItemStack =
    ItemStackBuilder(this).apply(builder).build()
