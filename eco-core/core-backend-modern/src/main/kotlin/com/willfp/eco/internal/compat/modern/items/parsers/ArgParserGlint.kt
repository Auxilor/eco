package com.willfp.eco.internal.compat.modern.items.parsers

import com.willfp.eco.internal.items.templates.FlagArgParser
import org.bukkit.inventory.meta.ItemMeta

object ArgParserGlint : FlagArgParser("glint") {
    override fun apply(meta: ItemMeta) {
        meta.setEnchantmentGlintOverride(true)
    }

    override fun test(meta: ItemMeta): Boolean {
        return meta.hasEnchantmentGlintOverride()
    }
}
