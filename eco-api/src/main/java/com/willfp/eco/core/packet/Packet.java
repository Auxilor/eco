package com.willfp.eco.core.packet;

import com.willfp.eco.core.Eco;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a packet.
 *
 * @param handle The NMS handle.
 */
public record Packet(@NotNull Object handle) {
    /**
     * Send to a player.
     *
     * @param player The player.
     */
    void send(@NotNull final Player player) {
        Eco.get().sendPacket(player, this);
    }
}
