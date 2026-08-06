package com.willfp.eco.core.packet;

import org.jetbrains.annotations.NotNull;

/**
 * Listens to packets.
 * <p>
 * Register listeners by returning them from
 * {@link com.willfp.eco.core.EcoPlugin#loadPacketListeners()}.
 */
public interface PacketListener {
    /**
     * Called when a packet is sent to a player.
     * <p>
     * Does nothing by default; override when needed.
     *
     * @param event The event.
     */
    default void onSend(@NotNull final PacketEvent event) {
        // Override when needed.
    }

    /**
     * Called when a packet is received from a player.
     * <p>
     * Does nothing by default; override when needed.
     *
     * @param event The event.
     */
    default void onReceive(@NotNull final PacketEvent event) {
        // Override when needed.
    }

    /**
     * Get the priority of the listener.
     *
     * @return The priority, {@link PacketPriority#NORMAL} by default.
     */
    default PacketPriority getPriority() {
        return PacketPriority.NORMAL;
    }
}
