package com.willfp.eco.core.items.args;

import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * Parse leather armor colors.
 */
public class ColorArgParser implements LookupArgParser {
    @Override
    public @Nullable Predicate<ItemStack> parseArguments(@NotNull final String[] args,
                                                         @NotNull final ItemMeta meta) {
        if (!(meta instanceof LeatherArmorMeta armorMeta)) {
            return null;
        }

        String color = null;

        for (String arg : args) {
            String[] argSplit = arg.split(":");
            if (!argSplit[0].equalsIgnoreCase("color")) {
                continue;
            }

            if (argSplit.length < 2) {
                continue;
            }

            color = argSplit[1];
        }

        if (color == null) {
            return null;
        }

        armorMeta.setColor(Color.fromRGB(Integer.parseInt(color, 16)));

        String finalColor = color;
        return test -> {
            if (!test.hasItemMeta()) {
                return false;
            }

            ItemMeta testMeta = test.getItemMeta();

            assert testMeta != null;

            if (testMeta instanceof LeatherArmorMeta cast) {
                return finalColor.equalsIgnoreCase(
                        Integer.toHexString(cast.getColor().getRed())
                                + Integer.toHexString(cast.getColor().getGreen())
                                + Integer.toHexString(cast.getColor().getBlue())
                );
            }

            return true;
        };
    }
}
