package com.willfp.eco.util.bukkit.keys;

import com.willfp.eco.util.internal.PluginDependent;
import com.willfp.eco.util.plugin.EcoPlugin;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public class NamespacedKeyFactory extends PluginDependent {
    /**
     * Factory class to produce {@link NamespacedKey}s associated with an {@link EcoPlugin}.
     *
     * @param plugin The plugin that this factory creates keys for.
     */
    public NamespacedKeyFactory(@NotNull final EcoPlugin plugin) {
        super(plugin);
    }

    /**
     * Create an {@link NamespacedKey} associated with an {@link EcoPlugin}.
     *
     * @param key The key in the {@link NamespacedKey}.
     * @return The created {@link NamespacedKey}.
     */
    public NamespacedKey create(@NotNull final String key) {
        return new NamespacedKey(this.getPlugin(), key);
    }
}
