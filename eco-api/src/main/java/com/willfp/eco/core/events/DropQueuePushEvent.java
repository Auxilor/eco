package com.willfp.eco.core.events;

import java.util.Collection;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a {@link com.willfp.eco.core.drops.DropQueue} is pushed, before any items
 * or experience are actually given to the player or dropped in the world.
 * <p>
 * The event is cancellable: cancelling it discards the entire push, so nothing is dropped
 * and no experience is given.
 * <p>
 * The items and experience can be modified with {@link #setItems(Collection)} and
 * {@link #setXp(int)}; the values left on the event when all handlers have run are the
 * ones that get dropped.
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
    private int xp;

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
     * Set the xp to be dropped.
     *
     * @param xp The xp.
     */
    public void setXp(int xp) {
        this.xp = xp;
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
     * Get if the push is telekinetic, i.e. if the drops go straight into the player's
     * inventory instead of being dropped in the world.
     * <p>
     * This is true if telekinesis was forced on the queue, or if the player passed the
     * telekinesis test and is allowed to pick items up at the location.
     *
     * @return The telekinesis state.
     */
    public boolean isTelekinetic() {
        return this.isTelekinetic;
    }
}
