package com.willfp.eco.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang.Validate;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

@UtilityClass
public class SkullUtils {
    /**
     * If the meta set function has been set.
     */
    private boolean initialized = false;

    /**
     * The meta set function.
     */
    private BiConsumer<SkullMeta, String> metaSetConsumer = null;

    /**
     * Set the texture of a skull from base64.
     *
     * @param meta   The meta to modify.
     * @param base64 The base64 texture.
     */
    public void setSkullTexture(@NotNull final SkullMeta meta,
                                @NotNull final String base64) {
        Validate.isTrue(initialized, "Must be initialized!");
        Validate.notNull(metaSetConsumer, "Must be initialized!");

        metaSetConsumer.accept(meta, base64);
    }

    /**
     * Initialize the skull texture function.
     *
     * @param function The function.
     */
    @ApiStatus.Internal
    public void initialize(@NotNull final BiConsumer<SkullMeta, String> function) {
        Validate.isTrue(!initialized, "Already initialized!");

        metaSetConsumer = function;
        initialized = true;
    }
}
