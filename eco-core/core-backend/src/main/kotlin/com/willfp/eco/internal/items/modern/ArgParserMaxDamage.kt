package com.willfp.eco.internal.items.modern

import com.willfp.eco.internal.items.templates.ValueArgParser
import com.willfp.eco.util.StringUtils
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta
import org.checkerframework.checker.units.qual.m

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
