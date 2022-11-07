package com.willfp.eco.internal.items

import com.willfp.eco.core.Eco
import com.willfp.eco.core.items.args.LookupArgParser
import com.willfp.eco.util.NamespacedKeyUtils
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import java.util.function.Predicate

object ArgParserUndyeable : LookupArgParser {
    override fun parseArguments(args: Array<out String>, meta: ItemMeta): Predicate<ItemStack>? {
        var undyeable = false

        for (arg in args) {
            if (arg.equals("undyeable", true)) {
                undyeable = true
            }
        }

        if (!undyeable) {
            return null
        }

        meta.isUndyeable = true

        return Predicate {
            val testMeta = it.itemMeta ?: return@Predicate false

            testMeta.isUndyeable
        }
    }

    override fun serializeBack(meta: ItemMeta): String? {
        if (!meta.isUndyeable) {
            return null
        }

        return "undyeable"
    }

    val key: NamespacedKey
        get() = NamespacedKeyUtils.fromString("eco:undyeable")

    var ItemMeta.isUndyeable: Boolean
        get() = this.persistentDataContainer.get(key, PersistentDataType.INTEGER) == 1
        set(value) {
            this.persistentDataContainer.set(key, PersistentDataType.INTEGER, if (value) 1 else 0)
        }
}