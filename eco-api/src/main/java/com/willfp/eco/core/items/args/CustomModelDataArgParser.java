package com.willfp.eco.core.items.args;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * Parse custom model data.
 *
 * @deprecated Moved to internals.
 */
@Deprecated(forRemoval = true)
public class CustomModelDataArgParser implements LookupArgParser {
    @Override
    public @Nullable Predicate<ItemStack> parseArguments(@NotNull final String[] args,
                                                         @NotNull final ItemMeta meta) {
        Integer modelData = null;

        for (String arg : args) {
            String[] argSplit = arg.split(":");
            if (!argSplit[0].equalsIgnoreCase("custom-model-data")) {
                continue;
            }

            if (argSplit.length < 2) {
                continue;
            }

            String asString = argSplit[1];

            try {
                modelData = Integer.parseInt(asString);
            } catch (NumberFormatException e) {
                modelData = null;
            }
        }

        if (modelData == null) {
            return null;
        }

        meta.setCustomModelData(modelData);

        int finalModelData = modelData;
        return test -> {
            if (!test.hasItemMeta()) {
                return false;
            }

            ItemMeta testMeta = test.getItemMeta();

            assert testMeta != null;

            return testMeta.getCustomModelData() == finalModelData;
        };
    }
}
