package com.willfp.eco.core.events;

import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerJumpEvent extends PlayerMoveEvent {
    /**
     * Internal bukkit.
     */
    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * Create a new PlayerJumpEvent.
     *
     * @param event The PlayerMoveEvent.
     */
    public PlayerJumpEvent(@NotNull final PlayerMoveEvent event) {
        super(event.getPlayer(), event.getFrom(), event.getTo());
    }

    /**
     * Internal bukkit.
     *
     * @return The handlers.
     */
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * Internal bukkit.
     *
     * @return The handlers.
     */
    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }
}
