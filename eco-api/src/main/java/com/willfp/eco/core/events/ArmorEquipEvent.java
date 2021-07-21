package com.willfp.eco.core.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * The armor equip event <b>does not contain information about the event.</b>
 * <p>
 * It is purely a trigger called whenever a player changes armor, you have to run
 * your own checks.
 * <p>
 * The event is called before the player's inventory actually updates,
 * so you can check a tick later to see the new contents.
 */
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
