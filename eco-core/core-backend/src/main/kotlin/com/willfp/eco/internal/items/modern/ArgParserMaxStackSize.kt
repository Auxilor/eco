package com.willfp.eco.internal.items.modern

import com.willfp.eco.internal.items.templates.ValueArgParser
import com.willfp.eco.util.StringUtils
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta
import org.checkerframework.checker.units.qual.m

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
