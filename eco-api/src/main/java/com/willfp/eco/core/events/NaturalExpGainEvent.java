package com.willfp.eco.core.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Event similar to {@link PlayerExpChangeEvent}, except it
 * isn't called if the exp is from a bottle.
 * <p>
 * It is called whenever a player gains experience from any other source, for example
 * from mining, smelting, breeding, fishing, or killing entities.
 * <p>
 * The event is not cancellable. To change or negate the experience gained, modify the
 * backing event with {@link #getExpChangeEvent()}.
 */
public class NaturalExpGainEvent extends Event {
    /**
     * Internal bukkit.
     */
    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * The associated {@link PlayerExpChangeEvent}.
     * Use this to modify event parameters.
     */
    private final PlayerExpChangeEvent expChangeEvent;

    /**
     * Create event based off parameters.
     *
     * @param event The associated PlayerExpChangeEvent.
     */
    public NaturalExpGainEvent(@NotNull final PlayerExpChangeEvent event) {
        this.expChangeEvent = event;
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
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * Get the event that caused this event.
     *
     * @return The exp change event.
     */
    public PlayerExpChangeEvent getExpChangeEvent() {
        return this.expChangeEvent;
    }
}
