package com.willfp.eco.internal.entities

import com.willfp.eco.core.entities.args.EntityArgParseResult
import com.willfp.eco.core.entities.args.EntityArgParser
import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.entity.Firework

object EntityArgParserFirework : EntityArgParser {

    private fun parseColor(input: String): Color? {
        if (!input.startsWith("#")) return null
        val hex = input.removePrefix("#").toIntOrNull(16) ?: return null
        return Color.fromRGB(hex)
    }

    private fun parseColors(input: String): List<Color>? {
        if (input.isBlank()) return emptyList()
        return input.split(",").mapNotNull { parseColor(it) }.takeIf { it.isNotEmpty() }
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

    override fun parseArguments(args: Array<out String>): EntityArgParseResult? {
        val effects = mutableListOf<Pair<Int, FireworkEffect>>()

        for (arg in args) {
            val effect = parseEffect(arg) ?: continue
            effects += effect
        }

        if (effects.isEmpty()) return null

        val sortedEffects = effects.sortedBy { it.first }

        return EntityArgParseResult(
            {
                return@EntityArgParseResult it is Firework
            },
            {
                (it as? Firework)?.let { firework ->
                    val meta = firework.fireworkMeta
                    meta.clearEffects()
                    sortedEffects.forEach { (_, effect) -> meta.addEffect(effect) }
                    firework.fireworkMeta = meta
                }
            }
        )
    }
}