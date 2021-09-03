package com.willfp.eco.core.items.args;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class EnchantmentArgParser implements LookupArgParser {
    @Override
    public Predicate<ItemStack> parseArguments(@NotNull final String[] args,
                                               @NotNull final ItemMeta meta) {
        Map<Enchantment, Integer> requiredEnchantments = new HashMap<>();

        for (String enchantArg : args) {
            String[] enchantArgSplit = enchantArg.split(":");

            Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantArgSplit[0].toLowerCase()));
            if (enchantment == null) {
                continue;
            }

            if (enchantArgSplit.length < 2) {
                continue;
            }

            int level = Integer.parseInt(enchantArgSplit[1]);

            requiredEnchantments.put(enchantment, level);
        }

        if (meta instanceof EnchantmentStorageMeta storageMeta) {
            requiredEnchantments.forEach((enchantment, integer) -> storageMeta.addStoredEnchant(enchantment, integer, true));
        } else {
            requiredEnchantments.forEach((enchantment, integer) -> meta.addEnchant(enchantment, integer, true));
        }

        return test -> {
            if (!test.hasItemMeta()) {
                return false;
            }

            ItemMeta testMeta = test.getItemMeta();

            assert testMeta != null;

            if (testMeta instanceof EnchantmentStorageMeta storageMeta) {
                for (Map.Entry<Enchantment, Integer> entry : requiredEnchantments.entrySet()) {
                    if (!storageMeta.hasStoredEnchant(entry.getKey())) {
                        return false;
                    }
                    if (storageMeta.getStoredEnchantLevel(entry.getKey()) < entry.getValue()) {
                        return false;
                    }
                }
            } else {
                for (Map.Entry<Enchantment, Integer> entry : requiredEnchantments.entrySet()) {
                    if (!testMeta.hasEnchant(entry.getKey())) {
                        return false;
                    }
                    if (testMeta.getEnchantLevel(entry.getKey()) < entry.getValue()) {
                        return false;
                    }
                }
            }

            return true;
        };
    }
}
