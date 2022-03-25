package com.willfp.eco.util

import com.willfp.eco.core.items.Items
import org.bukkit.inventory.ItemStack

/**
 * Convert an item to [Items] lookup string.
 *
 * @return The [Items] lookup string.
 */
fun ItemStack.toLookupString(): String {

    val result = StringBuilder("${this.type.name.lowercase()} ${this.amount}")

    val meta = this.itemMeta?: return result.toString()

    Items.getArgParsers().forEach {
        val parsed = it.toLookupString(meta)

        if (parsed != null) {
            result.append(" $parsed")
        }
    }

    return result.toString()
}