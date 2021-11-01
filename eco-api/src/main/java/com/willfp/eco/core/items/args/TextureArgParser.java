package com.willfp.eco.core.items.args;

import com.willfp.eco.util.SkullUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * Parse skull textures.
 */
public class TextureArgParser implements LookupArgParser {
    @Override
    public @Nullable Predicate<ItemStack> parseArguments(@NotNull final String[] args,
                                                         @NotNull final ItemMeta meta) {
        String skullTexture = null;

        for (String arg : args) {
            String[] argSplit = arg.split(":");
            if (!argSplit[0].equalsIgnoreCase("texture")) {
                continue;
            }

            if (argSplit.length < 2) {
                continue;
            }

            skullTexture = argSplit[1];
        }

        if (meta instanceof SkullMeta skullMeta && skullTexture != null) {
            SkullUtils.setSkullTexture(skullMeta, skullTexture);
        }

        if (skullTexture == null) {
            return null;
        }

        String finalSkullTexture = skullTexture;
        return test -> {
            if (!test.hasItemMeta()) {
                return false;
            }

            ItemMeta testMeta = test.getItemMeta();

            assert testMeta != null;

            if (testMeta instanceof SkullMeta skullMeta) {
                return finalSkullTexture.equalsIgnoreCase(SkullUtils.getSkullTexture(skullMeta));
            }

            return true;
        };
    }
}
