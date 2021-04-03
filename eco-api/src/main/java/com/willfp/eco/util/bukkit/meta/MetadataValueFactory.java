package com.willfp.eco.util.bukkit.meta;

import com.willfp.eco.util.internal.PluginDependent;
import com.willfp.eco.util.plugin.EcoPlugin;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

public class MetadataValueFactory extends PluginDependent {
    /**
     * Factory class to produce {@link FixedMetadataValue}s associated with an {@link EcoPlugin}.
     *
     * @param plugin The plugin that this factory creates values for.
     */
    public MetadataValueFactory(@NotNull final EcoPlugin plugin) {
        super(plugin);
    }

    /**
     * Create an {@link FixedMetadataValue} associated with an {@link EcoPlugin} with a specified value.
     *
     * @param value The value of meta key.
     * @return The created {@link FixedMetadataValue}.
     */
    public FixedMetadataValue create(@NotNull final Object value) {
        return new FixedMetadataValue(this.getPlugin(), value);
    }
}
