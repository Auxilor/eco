package com.willfp.eco.core.actionbar;

import com.willfp.eco.core.EcoPlugin;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

/**
 * A registered persistent action bar.
 */
public interface PersistentActionBar {
    /**
     * Get the key of the bar, as {@code plugin:id}.
     *
     * @return The key.
     */
    @NotNull
    NamespacedKey getKey();

    /**
     * Get the plugin that owns the bar.
     *
     * @return The plugin.
     */
    @NotNull
    EcoPlugin getPlugin();

    /**
     * Get the priority of the bar. Higher priorities show over lower ones.
     *
     * @return The priority.
     */
    int getPriority();

    /**
     * Unregister the bar.
     */
    void unregister();
}
