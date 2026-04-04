package com.willfp.eco.internal.items

import com.willfp.eco.core.items.args.LookupArgParser
import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.FireworkEffectMeta
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

    private fun serializeEffect(index: Int, effect: FireworkEffect): String {
        val type = effect.type.name.lowercase()
        val colors = effect.colors.joinToString(",") { String.format("#%06X", it.asRGB()) }
        val fade = if (effect.fadeColors.isEmpty()) "false"
        else effect.fadeColors.joinToString(",") { String.format("#%06X", it.asRGB()) }
        val trail = effect.hasTrail()
        val flicker = effect.hasFlicker()

        return "firework_effect:$index:$type:$colors:$fade:$trail:$flicker"
    }

    private fun effectsEqual(first: FireworkEffect, second: FireworkEffect): Boolean {
        return first.type == second.type &&
                first.colors.map { it.asRGB() } == second.colors.map { it.asRGB() } &&
                first.fadeColors.map { it.asRGB() } == second.fadeColors.map { it.asRGB() } &&
                first.hasTrail() == second.hasTrail() &&
                first.hasFlicker() == second.hasFlicker()
    }

    private fun parseEffect(arg: String): Pair<Int, FireworkEffect>? {
        val split = arg.split(":", limit = 7)
        if (!split[0].equals("firework_effect", ignoreCase = true)) return null
        if (split.size != 7) return null

        val index = split[1].toIntOrNull() ?: return null
        val type = try {
            FireworkEffect.Type.valueOf(split[2].uppercase())
        } catch (_: IllegalArgumentException) {
            return null
        }

        val colors = parseColors(split[3]) ?: return null
        val fadeColors = if (split[4].equals("false", ignoreCase = true)) emptyList()
        else parseColors(split[4]) ?: return null

        val trail = split[5].equals("true", ignoreCase = true)
        val flicker = split[6].equals("true", ignoreCase = true)

        val effect = FireworkEffect.builder()
            .with(type)
            .withColor(colors)
            .withFade(fadeColors)
            .trail(trail)
            .flicker(flicker)
            .build()

        return index to effect
    }

    private fun applyEffects(meta: ItemMeta, sortedEffects: List<Pair<Int, FireworkEffect>>) {
        when (meta) {
            is FireworkMeta -> {
                meta.clearEffects()
                sortedEffects.forEach { (_, effect) -> meta.addEffect(effect) }
            }

            is FireworkEffectMeta -> {
                // Firework stars can only store one effect, so pick the lowest indexed one.
                meta.effect = sortedEffects.first().second
            }
        }
    }

    private fun matchesEffects(
        item: ItemStack,
        baseMeta: ItemMeta,
        sortedEffects: List<Pair<Int, FireworkEffect>>
    ): Boolean {
        return when (baseMeta) {
            is FireworkMeta -> {
                val testMeta = item.itemMeta as? FireworkMeta ?: return false
                val testEffects = testMeta.effects
                if (testEffects.size != sortedEffects.size) return false

                sortedEffects.allIndexed { idx, (_, effect) ->
                    effectsEqual(testEffects[idx], effect)
                }
            }

            is FireworkEffectMeta -> {
                val testMeta = item.itemMeta as? FireworkEffectMeta ?: return false
                if (!testMeta.hasEffect()) return false
                val testEffect = testMeta.effect ?: return false
                effectsEqual(testEffect, sortedEffects.first().second)
            }

            else -> false
        }
    }

    override fun parseArguments(args: Array<out String>, meta: ItemMeta): Predicate<ItemStack>? {
        if (meta !is FireworkMeta && meta !is FireworkEffectMeta) return null

        val effects = mutableListOf<Pair<Int, FireworkEffect>>()

        for (arg in args) {
            val effect = parseEffect(arg) ?: continue
            effects += effect
        }

        if (effects.isEmpty()) return null

        val sortedEffects = effects.sortedBy { it.first }
        applyEffects(meta, sortedEffects)

        return Predicate { item -> matchesEffects(item, meta, sortedEffects) }
    }

    override fun serializeBack(meta: ItemMeta): String? {
        return when (meta) {
            is FireworkMeta -> {
                if (meta.effects.isEmpty()) return null
                meta.effects.mapIndexed { idx, effect ->
                    serializeEffect(idx, effect)
                }.joinToString("\n")
            }

            is FireworkEffectMeta -> {
                if (!meta.hasEffect()) return null
                val effect = meta.effect ?: return null
                serializeEffect(0, effect)
            }

            else -> null
        }
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