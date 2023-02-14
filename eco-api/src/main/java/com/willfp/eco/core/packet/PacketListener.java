package com.willfp.eco.core.packet;

import org.jetbrains.annotations.NotNull;

/**
 * Listens to packets.
 */
public interface PacketListener {
    /**
     * Called when a handle is sent.
     *
     * @param event The event.
     */
    default void onSend(@NotNull final PacketEvent event) {
        // Override when needed.
    }

    /**
     * Called when a handle is received.
     *
     * @param event The event.
     */
    default void onReceive(@NotNull final PacketEvent event) {
        // Override when needed.
    }

    /**
     * Get the priority of the listener.
     *
     * @return The priority.
     */
    default PacketPriority getPriority() {
        return PacketPriority.NORMAL;
    }
}
