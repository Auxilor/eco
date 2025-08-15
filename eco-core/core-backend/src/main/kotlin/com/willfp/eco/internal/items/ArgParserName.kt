package com.willfp.eco.internal.items

import com.willfp.eco.internal.items.templates.ValueArgParser
import com.willfp.eco.util.StringUtils
import org.bukkit.inventory.meta.ItemMeta

object ArgParserName : ValueArgParser<String>("name") {
    override fun parse(arg: String): String {
        return arg
    }

    override fun apply(meta: ItemMeta, value: String) {
        val formatted = StringUtils.format(value)

        // I don't know why it says it's redundant, the compiler yells at me
        @Suppress("UsePropertyAccessSyntax", "RedundantSuppression", "DEPRECATION")
        meta.setDisplayName(formatted)
    }

    override fun test(meta: ItemMeta): String? {
        if (!meta.hasDisplayName()) {
            return null
        }

        @Suppress("DEPRECATION")
        return meta.displayName
    }
}
