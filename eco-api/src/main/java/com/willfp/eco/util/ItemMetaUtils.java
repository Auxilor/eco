package com.willfp.eco.util;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.tuples.Pair;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utilities / API methods for the server.
 */
public final class ItemMetaUtils {
    /**
     * Set the armor trim for the given ItemMeta
     *
     * @param meta The given ItemMeta.
     * @param trim A pair of strings, where the first string represents the trim material name, and the second
     *             one represents the trim pattern name.
     */
    public static void setArmorTrim(@NotNull ItemMeta meta, @NotNull Pair<String, String> trim) {
        Eco.get().setArmorTrim(meta, trim);
    }

    /**
     * Get the armor trim for the given ItemMeta
     *
     * @param meta The given ItemMeta.
     * @return A pair of strings, where the first string represents the trim material name, and the second
     *             one represents the trim pattern name.
     */
    @Nullable
    public static Pair<String, String> getArmorTrim(@NotNull ItemMeta meta) {
        return Eco.get().getArmorTrim(meta);
    }

    private ItemMetaUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
