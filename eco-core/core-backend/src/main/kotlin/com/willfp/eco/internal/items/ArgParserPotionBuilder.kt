package com.willfp.eco.internal.items

import com.willfp.eco.core.items.args.LookupArgParser
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.function.Predicate
import kotlin.collections.plusAssign

object ArgParserPotionBuilder : LookupArgParser {

    override fun parseArguments(args: Array<out String>, meta: ItemMeta): Predicate<ItemStack>? {
        if (meta !is PotionMeta) return null

        val effects = mutableListOf<PotionEffect>()

        for (arg in args) {
            val split = arg.split(":", limit = 4)
            if (!split[0].equals("potion_effect", ignoreCase = true)) continue
            if (split.size != 4) continue

            @Suppress("DEPRECATION")
            val type = PotionEffectType.getByName(split[1].uppercase()) ?: continue
            val level = split[2].toIntOrNull() ?: continue
            val duration = split[3].toIntOrNull() ?: continue

            effects += PotionEffect(type, duration, level - 1)
        }

        if (effects.isEmpty()) return null

        for (effect in effects) {
            meta.addCustomEffect(effect, true)
        }

        return Predicate { item ->
            val testMeta = item.itemMeta as? PotionMeta ?: return@Predicate false

            effects.all { effect ->
                val existing = testMeta.customEffects
                    .firstOrNull { it.type == effect.type }
                    ?: return@all false

                existing.amplifier == effect.amplifier &&
                        existing.duration == effect.duration
            }
        }
    }

    override fun serializeBack(meta: ItemMeta): String? {
        val potionMeta = meta as? PotionMeta ?: return null
        if (potionMeta.customEffects.isEmpty()) return null

        return potionMeta.customEffects.joinToString("\n") { effect ->
            val typeName = effect.type.key.key.lowercase()
            val level = effect.amplifier + 1
            val duration = effect.duration
            "potion_effect:$typeName:$level:$duration"
        }
    }
}