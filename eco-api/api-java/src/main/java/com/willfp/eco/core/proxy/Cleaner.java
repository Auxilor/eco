package com.willfp.eco.core.proxy;

import com.willfp.eco.core.EcoPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * A cleaner is an internal component to fix classloader errors.
 * <p>
 * Important to allow for PlugMan/ServerUtils support.
 */
public interface Cleaner {
    /**
     * Clean up classes left over from plugin.
     *
     * @param plugin The plugin.
     */
    void clean(@NotNull EcoPlugin plugin);
}
