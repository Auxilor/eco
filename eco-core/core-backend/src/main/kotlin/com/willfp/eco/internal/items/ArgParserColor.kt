package com.willfp.eco.internal.items

import com.willfp.eco.core.items.args.LookupArgParser
import org.bukkit.Color
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.PotionMeta
import java.util.function.Predicate

object ArgParserColor : LookupArgParser {
    override fun parseArguments(args: Array<out String>, meta: ItemMeta): Predicate<ItemStack>? {
        val colorableMeta = when (meta) {
            is LeatherArmorMeta -> meta
            is PotionMeta -> meta
            else -> return null
        }

        var colorHex: String? = null

        for (arg in args) {
            val argSplit = arg.split(":")
            if (!argSplit[0].equals("color", ignoreCase = true)) {
                continue
            }
            if (argSplit.size < 2) {
                continue
            }
            colorHex = argSplit[1].replace("#", "")
        }

        colorHex ?: return null

        val rgb = Integer.parseInt(colorHex, 16)
        val bukkitColor = Color.fromRGB(rgb)
        
        when (colorableMeta) {
            is LeatherArmorMeta -> colorableMeta.setColor(bukkitColor)
            is PotionMeta -> colorableMeta.setColor(bukkitColor)
        }

        return Predicate { item ->
            val testMeta = item.itemMeta ?: return@Predicate false

            val testColor = when (testMeta) {
                is LeatherArmorMeta -> testMeta.color
                is PotionMeta -> testMeta.color
                else -> return@Predicate false
            } ?: return@Predicate false

            testColor.asRGB() == rgb
        }
    }

    override fun serializeBack(meta: ItemMeta): String? {
        val color = when (meta) {
            is LeatherArmorMeta -> meta.color
            is PotionMeta -> meta.color
            else -> return null
        } ?: return null

        return "color:#${Integer.toHexString(color.asRGB()).uppercase()}"
    }
}