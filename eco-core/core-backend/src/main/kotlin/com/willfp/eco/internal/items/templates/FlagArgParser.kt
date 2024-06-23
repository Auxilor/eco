package com.willfp.eco.internal.items.templates

import com.willfp.eco.core.items.args.LookupArgParser
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.util.function.Predicate

abstract class FlagArgParser(
    protected val flag: String
) : LookupArgParser {
    abstract fun apply(meta: ItemMeta)

    abstract fun test(meta: ItemMeta): Boolean

    override fun parseArguments(args: Array<out String>, meta: ItemMeta): Predicate<ItemStack>? {
        var has = false

        for (arg in args) {
            if (arg.equals(flag, true)) {
                has = true
            }
        }

        if (!has) {
            return null
        }

        apply(meta)

        return Predicate {
            val testMeta = it.itemMeta ?: return@Predicate false

            test(testMeta)
        }
    }

    override fun serializeBack(meta: ItemMeta): String? {
        if (!test(meta)) {
            return null
        }

        return flag
    }
}
