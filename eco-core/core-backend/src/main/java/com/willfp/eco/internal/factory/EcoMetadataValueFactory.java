package com.willfp.eco.internal.factory;

import com.willfp.eco.core.PluginDependent;
import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.factory.MetadataValueFactory;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

public class EcoMetadataValueFactory extends PluginDependent<EcoPlugin> implements MetadataValueFactory {
    public EcoMetadataValueFactory(@NotNull final EcoPlugin plugin) {
        super(plugin);
    }

    @Override
    public FixedMetadataValue create(@NotNull final Object value) {
        return new FixedMetadataValue(this.getPlugin(), value);
    }
}
