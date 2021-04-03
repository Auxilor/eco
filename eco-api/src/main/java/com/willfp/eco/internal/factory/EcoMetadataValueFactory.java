package com.willfp.eco.internal.factory;

import com.willfp.eco.core.PluginDependent;
import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.factory.MetadataValueFactory;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

public class EcoMetadataValueFactory extends PluginDependent implements MetadataValueFactory {
    /**
     * Factory class to produce {@link FixedMetadataValue}s associated with an {@link EcoPlugin}.
     *
     * @param plugin The plugin that this factory creates values for.
     */
    public EcoMetadataValueFactory(@NotNull final EcoPlugin plugin) {
        super(plugin);
    }

    /**
     * Create an {@link FixedMetadataValue} associated with an {@link EcoPlugin} with a specified value.
     *
     * @param value The value of meta key.
     * @return The created {@link FixedMetadataValue}.
     */
    @Override
    public FixedMetadataValue create(@NotNull final Object value) {
        return new FixedMetadataValue(this.getPlugin(), value);
    }
}
