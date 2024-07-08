package com.willfp.eco.internal.compat.modern.items.parsers

import com.willfp.eco.internal.items.templates.FlagArgParser
import org.bukkit.inventory.meta.ItemMeta

object ArgParserFireResistant : FlagArgParser("fire_resistant") {
    override fun apply(meta: ItemMeta) {
        meta.isFireResistant = true
    }

    override fun test(meta: ItemMeta): Boolean {
        return meta.isFireResistant
    }
}
