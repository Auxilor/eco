package com.willfp.eco.core.serialization;

import com.willfp.eco.core.config.interfaces.Config;
import org.jetbrains.annotations.NotNull;

/**
 * Serialize objects to configs.
 *
 * @param <T> The type of object to serialize.
 */
public interface ConfigSerializer<T> {
    /**
     * Serialize an object to a config.
     *
     * @param obj The object.
     * @return The config.
     */
    @NotNull
    Config serialize(@NotNull T obj);
}
