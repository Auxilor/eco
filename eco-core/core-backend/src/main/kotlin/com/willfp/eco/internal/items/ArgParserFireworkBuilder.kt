package com.willfp.eco.internal.items

import com.willfp.eco.core.items.args.LookupArgParser
import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.FireworkMeta
import org.bukkit.inventory.meta.ItemMeta
import java.util.function.Predicate

object ArgParserFireworkBuilder : LookupArgParser {

    private fun parseColor(input: String): Color? {
        if (!input.startsWith("#")) return null
        val hex = input.removePrefix("#").toIntOrNull(16) ?: return null
        return Color.fromRGB(hex)
    }

    private fun parseColors(input: String): List<Color>? {
        if (input.isBlank()) return emptyList()
        return input.split(",").mapNotNull { parseColor(it) }.takeIf { it.isNotEmpty() }
    }

    override fun parseArguments(args: Array<out String>, meta: ItemMeta): Predicate<ItemStack>? {
        if (meta !is FireworkMeta) return null

        val effects = mutableListOf<Pair<Int, FireworkEffect>>()

        for (arg in args) {
            val split = arg.split(":", limit = 7)
            if (!split[0].equals("firework_effect", ignoreCase = true)) continue
            if (split.size != 7) continue

            val index = split[1].toIntOrNull() ?: continue
            val type = try {
                FireworkEffect.Type.valueOf(split[2].uppercase())
            } catch (_: IllegalArgumentException) { continue }

            val colors = parseColors(split[3]) ?: continue
            val fadeColors = if (split[4].equals("false", ignoreCase = true)) emptyList()
            else parseColors(split[4]) ?: continue

            val trail = split[5].equals("true", ignoreCase = true)
            val flicker = split[6].equals("true", ignoreCase = true)

            val effect = FireworkEffect.builder()
                .with(type)
                .withColor(colors)
                .withFade(fadeColors)
                .trail(trail)
                .flicker(flicker)
                .build()

            effects += index to effect
        }

        if (effects.isEmpty()) return null

        val sortedEffects = effects.sortedBy { it.first }
        meta.clearEffects()
        sortedEffects.forEach { (_, effect) -> meta.addEffect(effect) }

        return Predicate { item ->
            val testMeta = item.itemMeta as? FireworkMeta ?: return@Predicate false
            val testEffects = testMeta.effects
            if (testEffects.size != sortedEffects.size) return@Predicate false

            sortedEffects.allIndexed { idx, (_, effect) ->
                val test = testEffects[idx]
                test.type == effect.type &&
                        test.colors.map { it.asRGB() } == effect.colors.map { it.asRGB() } &&
                        test.fadeColors.map { it.asRGB() } == effect.fadeColors.map { it.asRGB() } &&
                        test.hasTrail() == effect.hasTrail() &&
                        test.hasFlicker() == effect.hasFlicker()
            }
        }
    }

    override fun serializeBack(meta: ItemMeta): String? {
        val fireworkMeta = meta as? FireworkMeta ?: return null
        if (fireworkMeta.effects.isEmpty()) return null

        return fireworkMeta.effects.mapIndexed { idx, effect ->
            val type = effect.type.name.lowercase()
            val colors = effect.colors.joinToString(",") { String.format("#%06X", it.asRGB()) }
            val fade = if (effect.fadeColors.isEmpty()) "false"
            else effect.fadeColors.joinToString(",") { String.format("#%06X", it.asRGB()) }
            val trail = effect.hasTrail()
            val flicker = effect.hasFlicker()
            "firework_effect:$idx:$type:$colors:$fade:$trail:$flicker"
        }.joinToString("\n")
    }

    private inline fun <T> Iterable<T>.allIndexed(predicate: (Int, T) -> Boolean): Boolean {
        var idx = 0
        for (item in this) {
            if (!predicate(idx++, item)) return false
        }
        return true
    }
}

object ArgParserFireworkPower : LookupArgParser {

    override fun parseArguments(args: Array<out String>, meta: ItemMeta): Predicate<ItemStack>? {
        if (meta !is FireworkMeta) return null

        var power: Int? = null

        for (arg in args) {
            val split = arg.split(":")
            if (!split[0].equals("firework_power", ignoreCase = true)) continue
            if (split.size < 2) continue

            power = split[1].toIntOrNull()?.coerceIn(1, 3) ?: continue
        }

        if (power == null) return null

        meta.power = power

        return Predicate { item ->
            val testMeta = item.itemMeta as? FireworkMeta ?: return@Predicate false
            testMeta.power == power
        }
    }

    override fun serializeBack(meta: ItemMeta): String? {
        val fireworkMeta = meta as? FireworkMeta ?: return null
        return if (fireworkMeta.power > 0) {
            "firework_power:${fireworkMeta.power}"
        } else null
    }
}