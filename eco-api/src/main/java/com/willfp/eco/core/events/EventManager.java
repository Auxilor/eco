package com.willfp.eco.core.events;

import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

/**
 * Manages listeners for a plugin.
 */
public interface EventManager {
    /**
     * Register a listener with bukkit.
     *
     * @param listener The listener to register.
     */
    void registerListener(@NotNull Listener listener);

    /**
     * Unregister a listener with bukkit.
     *
     * @param listener The listener to unregister.
     */
    void unregisterListener(@NotNull Listener listener);

    /**
     * Unregister all listeners associated with the plugin.
     */
    void unregisterAllListeners();
}
