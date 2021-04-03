package com.willfp.eco.core.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;


public class ArmorEquipEvent extends PlayerEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    public ArmorEquipEvent(@NotNull final Player player) {
        super(player);
    }

    /**
     * Gets a list of handlers handling this event.
     *
     * @return A list of handlers handling this event.
     */

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
