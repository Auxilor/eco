package com.willfp.eco.core.events;

import com.willfp.eco.core.packet.PacketListener;
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

    /**
     * Register a packet listener.
     *
     * @param listener The listener.
     */
    void registerPacketListener(@NotNull PacketListener listener);
}
