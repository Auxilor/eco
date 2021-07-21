package com.willfp.eco.internal.factory;

import com.willfp.eco.core.PluginDependent;
import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.factory.NamespacedKeyFactory;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public class EcoNamespacedKeyFactory extends PluginDependent<EcoPlugin> implements NamespacedKeyFactory {
    public EcoNamespacedKeyFactory(@NotNull final EcoPlugin plugin) {
        super(plugin);
    }

    @Override
    public NamespacedKey create(@NotNull final String key) {
        return new NamespacedKey(this.getPlugin(), key);
    }
}
