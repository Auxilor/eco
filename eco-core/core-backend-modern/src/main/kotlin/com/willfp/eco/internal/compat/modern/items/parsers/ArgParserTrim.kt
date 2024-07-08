package com.willfp.eco.internal.compat.modern.items.parsers

import com.willfp.eco.core.items.args.LookupArgParser
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ArmorMeta
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.trim.ArmorTrim
import org.bukkit.inventory.meta.trim.TrimMaterial
import org.bukkit.inventory.meta.trim.TrimPattern
import java.util.function.Predicate

object ArgParserTrim : LookupArgParser {
    override fun parseArguments(args: Array<out String>, meta: ItemMeta): Predicate<ItemStack>? {
        if (meta !is ArmorMeta) return null

        var material: TrimMaterial? = null
        var pattern: TrimPattern? = null

        for (arg in args) {
            val argSplit = arg.split(":")
            if (!argSplit[0].equals("trim", ignoreCase = true)) {
                continue
            }

            @Suppress("DEPRECATION")
            material = Registry.TRIM_MATERIAL.get(NamespacedKey.minecraft(argSplit.getOrElse(1) {""}))

            @Suppress("DEPRECATION")
            pattern = Registry.TRIM_PATTERN.get(NamespacedKey.minecraft(argSplit.getOrElse(2) {""}))
        }

        if (material == null || pattern == null) return null


        meta.trim = ArmorTrim(material, pattern)

        return Predicate {
            val testMeta = it.itemMeta as? ArmorMeta ?: return@Predicate false
            val trim = testMeta.trim ?: return@Predicate false
            return@Predicate trim.material == material
                    && trim.pattern == pattern
        }
    }

    override fun serializeBack(meta: ItemMeta): String? {
        val trim = (meta as? ArmorMeta)?.trim ?: return null

        @Suppress("DEPRECATION")
        val materialKey = Registry.TRIM_MATERIAL.getKey(trim.material) ?: return null
        @Suppress("DEPRECATION")
        val patternKey = Registry.TRIM_PATTERN.getKey(trim.pattern) ?: return null

        return "trim:${materialKey.key.lowercase()}:${patternKey.key.lowercase()}"
    }
}
