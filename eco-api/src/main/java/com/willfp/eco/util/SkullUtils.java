package com.willfp.eco.util;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Utilities / API methods for player heads.
 */
public final class SkullUtils {
    /**
     * If the meta set function has been set.
     */
    private static boolean initialized = false;

    /**
     * The meta set function.
     */
    private static BiConsumer<SkullMeta, String> metaSetConsumer = null;

    /**
     * The meta get function.
     */
    private static Function<SkullMeta, String> metaGetConsumer = null;

    /**
     * Set the texture of a skull from base64.
     *
     * @param meta   The meta to modify.
     * @param base64 The base64 texture.
     */
    public static void setSkullTexture(@NotNull final SkullMeta meta,
                                @NotNull final String base64) {
        Validate.isTrue(initialized, "Must be initialized!");
        Validate.notNull(metaSetConsumer, "Must be initialized!");

        metaSetConsumer.accept(meta, base64);
    }

    /**
     * Get the texture of a skull - in base64.
     *
     * @param meta The meta to modify.
     * @return The texture, potentially null.
     */
    @Nullable
    public static String getSkullTexture(@NotNull final SkullMeta meta) {
        Validate.isTrue(initialized, "Must be initialized!");
        Validate.notNull(metaGetConsumer, "Must be initialized!");

        return metaGetConsumer.apply(meta);
    }

    /**
     * Initialize the skull texture function.
     *
     * @param function  The function.
     * @param function2 Get function.
     */
    @ApiStatus.Internal
    public static void initialize(@NotNull final BiConsumer<SkullMeta, String> function,
                           @NotNull final Function<SkullMeta, String> function2) {
        Validate.isTrue(!initialized, "Already initialized!");

        metaSetConsumer = function;
        metaGetConsumer = function2;
        initialized = true;
    }

    private SkullUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
