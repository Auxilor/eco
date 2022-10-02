package com.willfp.eco.core.proxy;

import com.willfp.eco.core.EcoPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * A cleaner is an internal component to fix classloader errors.
 * <p>
 * Important to allow for PlugMan/ServerUtils support.
 *
 * @deprecated No reason for this to be in the API.
 */
@Deprecated(since = "6.43.0", forRemoval = true)
public interface Cleaner {
    /**
     * Clean up classes left over from plugin.
     *
     * @param plugin The plugin.
     */
    void clean(@NotNull EcoPlugin plugin);
}
