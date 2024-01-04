package com.willfp.eco.internal.items

import com.willfp.eco.core.items.args.LookupArgParser
import com.willfp.eco.util.StringUtils
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.util.function.Predicate

object ArgParserName : LookupArgParser {
    override fun parseArguments(args: Array<out String>, meta: ItemMeta): Predicate<ItemStack>? {
        var name: String? = null

        for (arg in args) {
            if (!arg.lowercase().startsWith("name:")) {
                continue
            }
            name = arg.substring(5, arg.length)
        }

        name ?: return null

        val formatted = StringUtils.format(name)

        // I don't know why it says it's redundant, the compiler yells at me
        @Suppress("UsePropertyAccessSyntax", "RedundantSuppression", "DEPRECATION")
        meta.setDisplayName(formatted)

        return Predicate {
            val testMeta = it.itemMeta ?: return@Predicate false

            @Suppress("DEPRECATION")
            testMeta.displayName == formatted
        }
    }

    override fun serializeBack(meta: ItemMeta): String? {
        if (!meta.hasDisplayName()) {
            return null
        }

        @Suppress("DEPRECATION")
        return "name:\"${meta.displayName}\""
    }
}