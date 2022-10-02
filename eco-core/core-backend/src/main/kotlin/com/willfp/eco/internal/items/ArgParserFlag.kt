package com.willfp.eco.internal.items

import com.willfp.eco.core.items.args.LookupArgParser
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.util.function.Predicate

object ArgParserFlag : LookupArgParser {
    override fun parseArguments(args: Array<out String>, meta: ItemMeta): Predicate<ItemStack>? {
        val flags = mutableSetOf<ItemFlag>()

        for (arg in args) {
            val flag = runCatching { ItemFlag.valueOf(arg.uppercase()) }.getOrNull() ?: continue
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

    override fun serializeBack(meta: ItemMeta): String? {
        val flags = meta.itemFlags

        if (flags.isEmpty()) {
            return null
        }

        return flags.joinToString(" ") { it.name.lowercase() }
    }
}
