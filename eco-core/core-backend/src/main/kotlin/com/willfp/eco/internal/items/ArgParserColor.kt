package com.willfp.eco.internal.items

import com.willfp.eco.core.items.args.LookupArgParser
import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.*
import java.util.function.Predicate

object ArgParserColor : LookupArgParser {
    override fun parseArguments(args: Array<out String>, meta: ItemMeta): Predicate<ItemStack>? {
        val colorHex = args
            .map { it.split(":") }
            .firstNotNullOfOrNull {
                if (it[0].equals("color", true) && it.size >= 2) {
                    it[1].replace("#", "")
                } else null
            } ?: return null

        val rgb = Integer.parseInt(colorHex, 16)
        val color = Color.fromRGB(rgb)

        when (meta) {
            is LeatherArmorMeta -> meta.setColor(color)

            is PotionMeta -> meta.setColor(color)

            is FireworkEffectMeta -> {
                val effect = FireworkEffect.builder()
                    .withColor(color)
                    .build()
                meta.setEffect(effect)
            }

            else -> return null
        }

        return Predicate { item ->
            val testMeta = item.itemMeta ?: return@Predicate false

            val testColor = when (testMeta) {
                is LeatherArmorMeta -> testMeta.color
                is PotionMeta -> testMeta.color

                is FireworkEffectMeta -> {
                    if (item.type != Material.FIREWORK_STAR) return@Predicate false
                    testMeta.effect?.colors?.firstOrNull()
                }

                else -> null
            } ?: return@Predicate false

            testColor.asRGB() == rgb
        }
    }

    override fun serializeBack(meta: ItemMeta): String? {
        val color = when (meta) {
            is LeatherArmorMeta -> meta.color
            is PotionMeta -> meta.color
            is FireworkEffectMeta -> meta.effect?.colors?.firstOrNull()
            else -> null
        } ?: return null

        return "color:#${Integer.toHexString(color.asRGB()).uppercase()}"
    }
}