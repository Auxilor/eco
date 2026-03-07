package com.willfp.eco.internal.items

import com.willfp.eco.core.fast.fast
import com.willfp.eco.core.items.args.LookupArgParser
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.inventory.meta.ItemMeta
import java.util.function.Predicate

object ArgParserEnchantment : LookupArgParser {
    override fun parseArguments(args: Array<out String>, meta: ItemMeta): Predicate<ItemStack>? {
        val enchants = mutableMapOf<Enchantment, Int>()

        for (arg in args) {
            try {
                val argSplit = arg.split(":")

                val enchant = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT)
                    .get(NamespacedKey.minecraft(argSplit[0].lowercase())) ?: continue

                val level = argSplit.getOrNull(1)?.toIntOrNull() ?: enchant.maxLevel

                enchants[enchant] = level
            } catch (e: IllegalArgumentException) {
                continue
            }
        }

        if (enchants.isEmpty()) {
            return null
        }

        for ((enchant, level) in enchants) {
            if (meta is EnchantmentStorageMeta) {
                meta.addStoredEnchant(enchant, level, true)
            } else {
                meta.addEnchant(enchant, level, true)
            }
        }

        return Predicate {
            val onItem = it.fast().getEnchants(true)

            for ((enchant, level) in enchants) {
                if ((onItem[enchant] ?: 0) < level) {
                    return@Predicate false
                }
            }

            true
        }
    }

    override fun serializeBack(meta: ItemMeta): String? {
        val enchants = mutableMapOf<Enchantment, Int>()

        if (meta is EnchantmentStorageMeta) {
            enchants.putAll(meta.storedEnchants)
        } else {
            enchants.putAll(meta.enchants)
        }

        if (enchants.isEmpty()) {
            return null
        }

        val builder = StringBuilder()

        for ((enchant, level) in enchants) {
            builder.append("${enchant.key.key}:$level ")
        }

        return builder.toString().trimEnd()
    }
}