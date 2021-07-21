package com.willfp.eco.core.events;

import org.bukkit.event.Listener;

/**
 * Manages listeners for a plugin.
 */
public interface EventManager {
    /**
     * Register a listener with bukkit.
     *
     * @param listener The listener to register.
     */
    void registerListener(Listener listener);

    /**
     * Unregister a listener with bukkit.
     *
     * @param listener The listener to unregister.
     */
    void unregisterListener(Listener listener);

    /**
     * Unregister all listeners associated with the plugin.
     */
    void unregisterAllListeners();
}
