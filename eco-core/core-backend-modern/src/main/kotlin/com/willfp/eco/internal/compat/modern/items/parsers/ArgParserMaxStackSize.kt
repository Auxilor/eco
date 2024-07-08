package com.willfp.eco.internal.compat.modern.items.parsers

import com.willfp.eco.internal.items.templates.ValueArgParser
import org.bukkit.inventory.meta.ItemMeta

object ArgParserMaxStackSize : ValueArgParser<Int>("max_stack_size") {
    override fun parse(arg: String): Int? {
        return arg.toIntOrNull()
    }

    override fun apply(meta: ItemMeta, value: Int) {
        meta.setMaxStackSize(value)
    }

    override fun test(meta: ItemMeta): String? {
        if (!meta.hasMaxStackSize()) {
            return null
        }

        return meta.maxStackSize.toString()
    }
}
