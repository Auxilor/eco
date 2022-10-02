package com.willfp.eco.util;

import com.willfp.eco.core.Eco;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utilities / API methods for player heads.
 */
public final class SkullUtils {
    /**
     * Set the texture of a skull from base64.
     *
     * @param meta   The meta to modify.
     * @param base64 The base64 texture.
     */
    public static void setSkullTexture(@NotNull final SkullMeta meta,
                                       @NotNull final String base64) {
        Eco.get().setSkullTexture(meta, base64);
    }

    /**
     * Get the texture of a skull - in base64.
     *
     * @param meta The meta to modify.
     * @return The texture, potentially null.
     */
    @Nullable
    public static String getSkullTexture(@NotNull final SkullMeta meta) {
        return Eco.get().getSkullTexture(meta);
    }

    private SkullUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
