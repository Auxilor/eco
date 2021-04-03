package com.willfp.eco.core.factory;

import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

public interface MetadataValueFactory {
    FixedMetadataValue create(@NotNull Object value);
}
