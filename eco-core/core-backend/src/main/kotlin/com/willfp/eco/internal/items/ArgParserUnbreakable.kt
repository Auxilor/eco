package com.willfp.eco.internal.items

import com.willfp.eco.core.items.args.LookupArgParser
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.util.function.Predicate

class ArgParserUnbreakable : LookupArgParser {
    override fun parseArguments(args: Array<out String>, meta: ItemMeta): Predicate<ItemStack>? {
        var unbreakable = false

        for (arg in args) {
            if (arg.equals("unbreakable", true)) {
                unbreakable = true
            }
        }

        if (!unbreakable) {
            return null
        }

        meta.isUnbreakable = true

        return Predicate {
            val testMeta = it.itemMeta ?: return@Predicate false

            testMeta.isUnbreakable
        }
    }

    override fun toLookupString(meta: ItemMeta): String? {
        return if (meta.hasDisplayName()) {
            "name:\"${meta.displayName}\""
        } else {
            null
        }
    }
}