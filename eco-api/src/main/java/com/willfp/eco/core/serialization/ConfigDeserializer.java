package com.willfp.eco.core.serialization;

import com.willfp.eco.core.config.interfaces.Config;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Deserialize objects from configs.
 *
 * @param <T> The type of object to deserialize.
 */
public interface ConfigDeserializer<T> {
    /**
     * Deserialize a config to an object.
     *
     * @param config The config.
     * @return The object, or null if invalid.
     */
    @Nullable
    T deserialize(@NotNull Config config);
}
