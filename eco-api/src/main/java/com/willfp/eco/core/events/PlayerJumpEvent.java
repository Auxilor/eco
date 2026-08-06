package com.willfp.eco.core.events;

import org.bukkit.Location;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Event called when a player jumps.
 * <p>
 * On Paper it is fired from Paper's own jump event; on Spigot the jump is detected from
 * {@link PlayerMoveEvent} by inspecting the player's upwards velocity.
 * <p>
 * All state (cancellation, from, to) is delegated to the {@link PlayerMoveEvent} passed to
 * the constructor, so cancelling this event only cancels the movement when that event is a
 * real, live move event, which is the case on Spigot but not on Paper.
 */
public class PlayerJumpEvent extends PlayerMoveEvent {
    /**
     * Internal bukkit.
     */
    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * The handled event.
     */
    private final PlayerMoveEvent handle;

    /**
     * Create a new PlayerJumpEvent.
     *
     * @param event The PlayerMoveEvent.
     */
    public PlayerJumpEvent(@NotNull final PlayerMoveEvent event) {
        super(event.getPlayer(), event.getFrom(), event.getTo());

        this.handle = event;
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

    @Override
    public boolean isCancelled() {
        return handle.isCancelled();
    }

    @Override
    public void setCancelled(final boolean cancel) {
        handle.setCancelled(cancel);
    }

    @NotNull
    @Override
    public Location getFrom() {
        return handle.getFrom();
    }

    @Override
    public void setFrom(@NotNull final Location from) {
        handle.setFrom(from);
    }

    @Nullable
    @Override
    public Location getTo() {
        return handle.getTo();
    }

    @Override
    public void setTo(@NotNull final Location to) {
        handle.setTo(to);
    }
}
