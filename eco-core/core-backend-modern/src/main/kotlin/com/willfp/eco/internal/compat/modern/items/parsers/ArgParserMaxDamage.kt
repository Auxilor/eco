package com.willfp.eco.internal.compat.modern.items.parsers

import com.willfp.eco.internal.items.templates.ValueArgParser
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta

object ArgParserMaxDamage : ValueArgParser<Int>("max_damage") {
    override fun parse(arg: String): Int? {
        return arg.toIntOrNull()
    }

    override fun apply(meta: ItemMeta, value: Int) {
        if (meta !is Damageable) {
            return
        }

        meta.setMaxDamage(value)
    }

    override fun test(meta: ItemMeta): String? {
        if (meta !is Damageable) {
            return null
        }

        if (!meta.hasMaxDamage()) {
            return null
        }

        return meta.maxDamage.toString()
    }
}
