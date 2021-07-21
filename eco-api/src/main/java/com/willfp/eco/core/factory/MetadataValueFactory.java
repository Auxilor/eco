package com.willfp.eco.core.factory;

import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

/**
 * Factory to create metadata values for a specific plugin.
 */
public interface MetadataValueFactory {
    /**
     * Create a metadata value for a given plugin and object.
     *
     * @param value The object to store in metadata.
     * @return The metadata value.
     */
    FixedMetadataValue create(@NotNull Object value);
}
