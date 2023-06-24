package com.willfp.eco.internal.items

import com.willfp.eco.core.items.args.LookupArgParser
import com.willfp.eco.core.tuples.Pair
import com.willfp.eco.util.ItemMetaUtils
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.util.function.Predicate

object ArgParserTrim : LookupArgParser {
    override fun parseArguments(args: Array<out String>, meta: ItemMeta): Predicate<ItemStack>? {
        var material: String? = null
        var pattern: String? = null

        for (arg in args) {
            val argSplit = arg.split(":")
            if (!argSplit[0].equals("trim", ignoreCase = true)) {
                continue
            }
            if (argSplit.size < 3) {
                continue
            }
            material = argSplit[1]
            pattern = argSplit[2]
        }

        material ?: pattern ?: return null

        ItemMetaUtils.setArmorTrim(meta, Pair(material, pattern))

        return Predicate {
            val testMeta = it.itemMeta ?: return@Predicate false
            val trim = ItemMetaUtils.getArmorTrim(testMeta) ?: return@Predicate false
            return@Predicate trim.first!!.equals(material, true)
                    && trim.second!!.equals(pattern, true)
        }
    }

    override fun serializeBack(meta: ItemMeta): String? {
        val trim = ItemMetaUtils.getArmorTrim(meta) ?: return null

        return "trim:${trim.first}:${trim.second}"
    }
}