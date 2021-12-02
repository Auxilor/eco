package com.willfp.eco.core.items.args;

import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class LeatherArmorColorArgParser implements LookupArgParser {
    @Override
    public @Nullable Predicate<ItemStack> parseArguments(@NotNull String[] args, @NotNull ItemMeta meta) {

        if (!(meta instanceof LeatherArmorMeta)) return pred -> false;

        for (String arg : args) {
            String[] argSplit = arg.split(":");
            if (!argSplit[0].equalsIgnoreCase("color")) {
                continue;
            }
            if (argSplit.length < 2) {
                continue;
            }
            String asString = argSplit[1];

            LeatherArmorMeta lMeta = (LeatherArmorMeta) meta;

            lMeta.setColor(fromString(asString));

            return pred -> true;
        }

        return pred -> false;

    }

    private Color fromString(String color) {

        if (color.contains(",")) {

            String[] split = color.split(",");

            int red = 0;
            int green = 0;
            int blue = 0;

            if (split.length > 0) {
                red = Integer.parseInt(split[0]);
            }
            if (split.length > 1) {
                green = Integer.parseInt(split[1]);
            }
            if (split.length > 2) {
                blue = Integer.parseInt(split[2]);
            }

            return Color.fromRGB(red, green, blue);

        } else if (color.startsWith("#")) {

            java.awt.Color from = java.awt.Color.decode(color);

            return Color.fromRGB(from.getRed(), from.getGreen(), from.getBlue());

        } else {
            return fromString("#"+color);
        }

    }

}
