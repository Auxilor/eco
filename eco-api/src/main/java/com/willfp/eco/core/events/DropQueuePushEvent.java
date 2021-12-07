package com.willfp.eco.core.events;

import com.willfp.eco.core.drops.DropQueue;
import com.willfp.eco.core.drops.InternalDropQueue;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class DropQueuePushEvent extends PlayerEvent implements Cancellable {

    /**
     * Queue handle.
     */
    private final InternalDropQueue handle;

    /**
     * Queue itself.
     */
    private final DropQueue queue;

    /**
     * Cancel state.
     */
    private boolean cancelled;

    /**
     * Force telekinesis state
     */
    private final boolean forceTelekinesis;

    /**
     * Bukkit parity.
     */
    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * Create a new ArmorEquipEvent.
     *
     * @param player The player.
     */
    public DropQueuePushEvent(@NotNull final Player player, @NotNull final InternalDropQueue handle, @NotNull final DropQueue queue, final boolean forceTelekinesis) {
        super(player);
        this.handle = handle;
        this.queue = queue;
        this.forceTelekinesis = forceTelekinesis;
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
     * @param b The state.
     */
    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    /**
     * Get queue handle.
     *
     * @return The handle.
     */
    public InternalDropQueue getHandle() {
        return handle;
    }

    /**
     * Get queue.
     *
     * @return The queue.
     */
    public DropQueue getQueue() {
        return queue;
    }

    /**
     * Get force telekinesis state.
     *
     * @return The force telekinesis state.
     */
    public boolean isForceTelekinesis() {
        return this.forceTelekinesis;
    }

}
