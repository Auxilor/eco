package com.willfp.eco.internal.items

import com.willfp.eco.core.items.args.LookupArgParser
import org.bukkit.Color
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.LeatherArmorMeta
import java.util.function.Predicate

object ArgParserColor : LookupArgParser {
    override fun parseArguments(args: Array<out String>, meta: ItemMeta): Predicate<ItemStack>? {
        if (meta !is LeatherArmorMeta) {
            return null
        }

        var color: String? = null

        for (arg in args) {
            val argSplit = arg.split(":")
            if (!argSplit[0].equals("color", ignoreCase = true)) {
                continue
            }
            if (argSplit.size < 2) {
                continue
            }
            color = argSplit[1].replace("#","")
        }

        color ?: return null

        meta.setColor(Color.fromRGB(Integer.parseInt(color, 16)))

        return Predicate {
            val testMeta = it.itemMeta as? LeatherArmorMeta ?: return@Predicate false

            color.equals(
                Integer.toHexString(testMeta.color.red)
                        + Integer.toHexString(testMeta.color.green)
                        + Integer.toHexString(testMeta.color.blue),
                ignoreCase = true
            )
        }
    }

    override fun serializeBack(meta: ItemMeta): String? {
        if (meta !is LeatherArmorMeta) {
            return null
        }

        return "color:#${Integer.toHexString(meta.color.asRGB())}"
    }
}