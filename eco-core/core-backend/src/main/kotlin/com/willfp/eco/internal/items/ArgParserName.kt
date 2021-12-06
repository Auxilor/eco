package com.willfp.eco.internal.items

import com.willfp.eco.core.items.args.LookupArgParser
import com.willfp.eco.util.StringUtils
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.util.function.Predicate

class ArgParserName : LookupArgParser {
    override fun parseArguments(args: Array<out String>, meta: ItemMeta): Predicate<ItemStack>? {
        var name: String? = null

        for (arg in args) {
            val argSplit = arg.split(":")
            if (!argSplit[0].equals("name", ignoreCase = true)) {
                continue
            }
            if (argSplit.size < 2) {
                continue
            }
            name = argSplit[1].replace("_", "")
        }

        name ?: return null

        val formatted = StringUtils.format(name)

        meta.setDisplayName(formatted)

        return Predicate {
            val testMeta = it.itemMeta ?: return@Predicate false

            testMeta.displayName == formatted
        }
    }
}