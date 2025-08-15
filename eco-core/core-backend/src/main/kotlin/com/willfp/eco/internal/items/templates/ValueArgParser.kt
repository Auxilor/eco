package com.willfp.eco.internal.items.templates

import com.willfp.eco.core.items.args.LookupArgParser
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.util.function.Predicate

abstract class ValueArgParser<T: Any>(
    protected val flag: String
) : LookupArgParser {
    abstract fun parse(arg: String): T?

    abstract fun apply(meta: ItemMeta, value: T)

    abstract fun test(meta: ItemMeta): String?

    override fun parseArguments(args: Array<out String>, meta: ItemMeta): Predicate<ItemStack>? {
        var argument: String? = null

        for (arg in args) {
            if (!arg.lowercase().startsWith("${flag}:")) {
                continue
            }
            argument = arg.substring(flag.length + 1, arg.length)
        }

        argument ?: return null

        val parsed = parse(argument) ?: return null

        apply(meta, parsed)

        return Predicate {
            val testMeta = it.itemMeta ?: return@Predicate false

            test(testMeta) == parsed
        }
    }

    override fun serializeBack(meta: ItemMeta): String? {
        val test = test(meta)

        if (test.isNullOrBlank()) {
            return null
        }

        return "${flag}:\"$test\""
    }
}
