package com.willfp.eco.core.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Event called when a player opens their advancements screen.
 * <p>
 * The advancements screen is drawn by the client, so this is detected from the packet the
 * client sends when the screen opens. Switching tabs while the screen is open does not
 * call this event again.
 * <p>
 * This event is asynchronous: it is called from the player's network thread, so listeners
 * must schedule any work that touches the world onto the appropriate thread.
 */
public class PlayerOpenAdvancementsEvent extends Event {
    /**
     * Internal bukkit.
     */
    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * The player.
     */
    private final Player player;

    /**
     * Create a new PlayerOpenAdvancementsEvent.
     *
     * @param player The player.
     */
    public PlayerOpenAdvancementsEvent(@NotNull final Player player) {
        super(true);

        this.player = player;
    }

    /**
     * Get the player who opened their advancements screen.
     *
     * @return The player.
     */
    @NotNull
    public Player getPlayer() {
        return player;
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
