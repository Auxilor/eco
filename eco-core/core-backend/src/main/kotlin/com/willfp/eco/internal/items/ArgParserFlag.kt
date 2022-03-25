package com.willfp.eco.internal.items

import com.willfp.eco.core.items.args.LookupArgParser
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.inventory.meta.ItemMeta
import java.util.function.Predicate

class ArgParserFlag : LookupArgParser {
    override fun parseArguments(args: Array<out String>, meta: ItemMeta): Predicate<ItemStack>? {
        val flags = mutableSetOf<ItemFlag>()

        for (arg in args) {
            val flag = kotlin.runCatching { ItemFlag.valueOf(arg.uppercase()) }.getOrNull() ?: continue
            flags.add(flag)
        }

        if (flags.isEmpty()) {
            return null
        }

        meta.addItemFlags(*flags.toTypedArray())

        return Predicate {
            val testMeta = it.itemMeta ?: return@Predicate false

            testMeta.itemFlags.containsAll(flags)
        }
    }

    override fun toLookupString(meta: ItemMeta): String? {
        val flags = meta.itemFlags

        if (flags.isEmpty()) {
            return null
        }

        val result = StringBuilder()

        flags.forEach {
            result.append(it.name.lowercase())
            if (it != flags.last()) {
                result.append(" ")
            }
        }

        return result.toString()
    }
}