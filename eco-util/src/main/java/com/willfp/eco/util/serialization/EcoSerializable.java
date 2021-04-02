package com.willfp.eco.util.serialization;

import com.willfp.eco.util.config.Config;
import org.jetbrains.annotations.NotNull;

public interface EcoSerializable<T> {
    /**
     * Serialize an object into a config.
     *
     * @return The config.
     */
    @NotNull
    Config serialize();
}
