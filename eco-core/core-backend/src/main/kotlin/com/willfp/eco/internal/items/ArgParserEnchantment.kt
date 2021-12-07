package com.willfp.eco.internal.items

import com.willfp.eco.core.fast.FastItemStack
import com.willfp.eco.core.items.args.LookupArgParser
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.inventory.meta.ItemMeta
import java.util.function.Predicate

class ArgParserEnchantment : LookupArgParser {
    override fun parseArguments(args: Array<out String>, meta: ItemMeta): Predicate<ItemStack>? {
        val enchants = mutableMapOf<Enchantment, Int>()

        for (arg in args) {
            val argSplit = arg.split(":")

            if (argSplit.size < 2) {
                continue
            }

            val enchant = Enchantment.getByKey(NamespacedKey.minecraft(argSplit[0].lowercase()))
            val level = argSplit[1].toIntOrNull()

            if (enchant != null && level != null) {
                enchants[enchant] = level
            }
        }

        for ((enchant, level) in enchants) {
            if (meta is EnchantmentStorageMeta) {
                meta.addStoredEnchant(enchant, level, true)
            } else {
                meta.addEnchant(enchant, level, true)
            }
        }

        return Predicate {
            val onItem = FastItemStack.wrap(it).getEnchantmentsOnItem(true)

            for ((enchant, level) in enchants) {
                if ((onItem[enchant] ?: 0) < level) {
                    return@Predicate false
                }
            }

            true
        }
    }
}