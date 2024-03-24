package com.willfp.eco.core.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Called on DropQueue push.
 */
public class DropQueuePushEvent extends PlayerEvent implements Cancellable {
    /**
     * Cancel state.
     */
    private boolean cancelled;

    /**
     * If telekinetic.
     */
    private final boolean isTelekinetic;

    /**
     * The items.
     */
    private Collection<? extends ItemStack> items;

    /**
     * The xp.
     */
    private final int xp;

    /**
     * The location.
     */
    private final Location location;

    /**
     * Bukkit parity.
     */
    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * Create a new DropQueuePushEvent.
     *
     * @param player        The player.
     * @param items         The items.
     * @param location      The location.
     * @param xp            The xp.
     * @param isTelekinetic If the event is telekinetic.
     */
    public DropQueuePushEvent(@NotNull final Player player,
                              @NotNull final Collection<? extends ItemStack> items,
                              @NotNull final Location location,
                              final int xp,
                              final boolean isTelekinetic) {
        super(player);
        this.items = items;
        this.location = location;
        this.xp = xp;
        this.isTelekinetic = isTelekinetic;
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

    /**
     * Bukkit parity.
     *
     * @return The handler list.
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * Get cancel state.
     *
     * @return The cancel state.
     */
    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    /**
     * Set cancel state.
     *
     * @param cancelled If cancelled.
     */
    @Override
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * Get the items to be dropped.
     *
     * @return The items.
     */
    public Collection<? extends ItemStack> getItems() {
        return items;
    }

    /**
     * Set the items to be dropped.
     *
     * @param items The items.
     */
    public void setItems(Collection<? extends ItemStack> items) {
        this.items = items;
    }

    /**
     * Get the xp to be dropped.
     *
     * @return The xp.
     */
    public int getXp() {
        return xp;
    }

    /**
     * Get the location.
     *
     * @return The location.
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Get force telekinesis state.
     *
     * @return The force telekinesis state.
     */
    public boolean isTelekinetic() {
        return this.isTelekinetic;
    }
}
