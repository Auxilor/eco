package com.willfp.eco.core.proxy;

import com.willfp.eco.core.EcoPlugin;
import org.jetbrains.annotations.NotNull;

public interface Cleaner {
    /**
     * Clean up classes left over from plugin.
     *
     * @param plugin The plugin.
     */
    void clean(@NotNull EcoPlugin plugin);
}
