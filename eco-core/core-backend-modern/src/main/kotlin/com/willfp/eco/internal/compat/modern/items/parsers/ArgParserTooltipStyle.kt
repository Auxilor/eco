package com.willfp.eco.internal.compat.modern.items.parsers

import com.willfp.eco.core.items.args.LookupArgParser
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.util.function.Predicate

object ArgParserTooltipStyle : LookupArgParser {
    override fun parseArguments(args: Array<out String>, meta: ItemMeta): Predicate<ItemStack>? {
        val arg = args.firstOrNull {
            it.startsWith("tooltip-style:", ignoreCase = true)
                    || it.startsWith("tooltip_style:", ignoreCase = true)
        } ?: return null

        val parts = arg.substringAfter(":").split(":")
        val (namespace, key) = when (parts.size) {
            1 -> "minecraft" to parts[0] // default namespace
            2 -> parts[0] to parts[1]
            else -> return null
        }

        val namespacedKey = NamespacedKey(namespace, key)

        meta.setTooltipStyle(namespacedKey)

        return Predicate { stack ->
            val testMeta = stack.itemMeta ?: return@Predicate false
            testMeta.tooltipStyle == namespacedKey
        }
    }

    override fun serializeBack(meta: ItemMeta): String? {
        val style = meta.tooltipStyle ?: return null
        return "tooltip-style:${style.namespace}:${style.key}"
    }
}
