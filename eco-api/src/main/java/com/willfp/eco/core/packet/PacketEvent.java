package com.willfp.eco.core.packet;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a handle being sent or received.
 */
public class PacketEvent implements Cancellable {
    /**
     * The handle.
     */
    private final Packet packet;

    /**
     * The player.
     */
    private final Player player;

    /**
     * If the event should be cancelled.
     */
    private boolean cancelled = false;

    /**
     * Create a new handle event.
     *
     * @param packet The handle.
     * @param player The player.
     */
    public PacketEvent(@NotNull final Packet packet,
                       @NotNull final Player player) {
        this.packet = packet;
        this.player = player;
    }

    /**
     * Get the NMS handle.
     *
     * @return The handle.
     */
    @NotNull
    public Packet getPacket() {
        return packet;
    }

    /**
     * Get the player.
     *
     * @return The player.
     */
    @NotNull
    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }
}
