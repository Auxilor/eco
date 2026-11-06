package com.willfp.eco.core.items;

import java.util.Map;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Checks for whether a lookup string keeps the custom data of an item, and the SNBT
 * used when it doesn't.
 */
final class ItemCustomData {
    /**
     * The component holding plugin data, including the persistent data container.
     */
    private static final String CUSTOM_DATA_COMPONENT = "minecraft:custom_data";

    /**
     * Get an ItemStack as SNBT on one line.
     *
     * @param itemStack The ItemStack.
     * @return The SNBT.
     */
    @NotNull
    static String toCompactSNBT(@NotNull final ItemStack itemStack) {
        return compact(Items.toSNBT(itemStack));
    }

    /**
     * Remove the whitespace from SNBT, other than inside strings.
     */
    @NotNull
    private static String compact(@NotNull final String snbt) {
        StringBuilder builder = new StringBuilder(snbt.length());
        char quote = 0;
        boolean escaped = false;

        for (char character : snbt.toCharArray()) {
            if (quote == 0 && Character.isWhitespace(character)) {
                continue;
            }

            builder.append(character);

            if (quote == 0) {
                quote = character == '"' || character == '\'' ? character : 0;
            } else if (escaped) {
                escaped = false;
            } else if (character == '\\') {
                escaped = true;
            } else if (character == quote) {
                quote = 0;
            }
        }

        return builder.toString();
    }

    /**
     * Get if a lookup string keeps the custom data of an ItemStack.
     *
     * @param lookup    The lookup string.
     * @param itemStack The ItemStack.
     * @return If every key of the custom data is on the item from the lookup string.
     */
    static boolean isKeptBy(@NotNull final String lookup,
                            @NotNull final ItemStack itemStack) {
        Object expected = Items.getComponent(itemStack, CUSTOM_DATA_COMPONENT);
        if (expected == null) {
            return true;
        }

        ItemStack resolved = Items.lookup(lookup).getItem();
        return !resolved.getType().isAir()
                && containsKeys(Items.getComponent(resolved, CUSTOM_DATA_COMPONENT), expected);
    }

    /**
     * Only keys are compared, as the values of custom data can change per stack, like the
     * uses left on an item.
     */
    private static boolean containsKeys(@Nullable final Object actual,
                                        @NotNull final Object expected) {
        if (!(expected instanceof Map<?, ?> expectedMap)) {
            return actual != null;
        }

        if (!(actual instanceof Map<?, ?> actualMap)) {
            return false;
        }

        for (Map.Entry<?, ?> entry : expectedMap.entrySet()) {
            if (!actualMap.containsKey(entry.getKey())
                    || !containsKeys(actualMap.get(entry.getKey()), entry.getValue())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Prevent instantiation of this utility class.
     */
    private ItemCustomData() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
