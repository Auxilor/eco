package com.willfp.eco.util.serialization;

import com.willfp.eco.util.config.Config;
import org.jetbrains.annotations.NotNull;

public abstract class Deserializer<T extends EcoSerializable<T>> {
    /**
     * Deserialize a config into an object.
     *
     * @param config The config.
     * @return The object.
     */
    public abstract T deserialize(@NotNull Config config);
}
