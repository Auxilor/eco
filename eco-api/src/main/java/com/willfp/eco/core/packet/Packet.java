package com.willfp.eco.core.packet;

import com.willfp.eco.core.Eco;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a packet.
 */
public class Packet {
    /**
     * The packet handle.
     */
    private Object handle;

    /**
     * Create a new packet.
     *
     * @param handle The packet handle.
     */
    public Packet(@NotNull final Object handle) {
        this.handle = handle;
    }

    /**
     * Get the packet handle.
     *
     * @return The packet handle.
     */
    public Object getHandle() {
        return handle;
    }

    /**
     * Set the packet handle.
     *
     * @param handle The packet handle.
     */
    public void setHandle(@NotNull final Object handle) {
        this.handle = handle;
    }

    /**
     * Get the packet handle, compatible with the old record-based packet system.
     *
     * @return The packet handle.
     * @deprecated Use {@link #getHandle()} instead.
     */
    @Deprecated
    public Object handle() {
        return handle;
    }

    /**
     * Send to a player.
     *
     * @param player The player.
     */
    void send(@NotNull final Player player) {
        Eco.get().sendPacket(player, this);
    }
}
