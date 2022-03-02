package com.willfp.eco.core.serialization;

import com.willfp.eco.core.config.interfaces.Config;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Deserialize objects from configs.
 * <p>
 * Deserializers should <b>never</b> throw errors due to invalid configs,
 * all edge cases must be covered, and all failures must be encapsulated as null.
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
