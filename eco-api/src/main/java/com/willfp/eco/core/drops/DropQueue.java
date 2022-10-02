package com.willfp.eco.core.drops;

import com.willfp.eco.core.Eco;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * A {@link DropQueue} is a set of drops linked to player.
 * <p>
 * All drops should be passed through a drop queue for telekinesis integration.
 * <p>
 * It functions essentially as a builder class, and runs very quickly.
 *
 * @see com.willfp.eco.util.TelekinesisUtils
 */
public class DropQueue {
    /**
     * The internally used {@link DropQueue}.
     */
    private final DropQueue delegate;

    /**
     * Create a new DropQueue.
     *
     * @param player The player.
     */
    public DropQueue(@NotNull final Player player) {
        this.delegate = Eco.get().createDropQueue(player);
    }

    /**
     * Create a new DropQueue with no delegate.
     * <p>
     * Call this constructor if you're creating custom DropQueue
     * implementations.
     */
    protected DropQueue() {
        this.delegate = null;
    }

    /**
     * Add item to queue.
     *
     * @param item The item to add.
     * @return The DropQueue.
     */
    public DropQueue addItem(@NotNull final ItemStack item) {
        if (delegate == null) {
            return this;
        }

        delegate.addItem(item);
        return this;
    }

    /**
     * Add multiple items to queue.
     *
     * @param itemStacks The items to add.
     * @return The DropQueue.
     */
    public DropQueue addItems(@NotNull final Collection<ItemStack> itemStacks) {
        if (delegate == null) {
            return this;
        }

        delegate.addItems(itemStacks);
        return this;
    }

    /**
     * Add xp to queue.
     *
     * @param amount The amount to add.
     * @return The DropQueue.
     */
    public DropQueue addXP(final int amount) {
        if (delegate == null) {
            return this;
        }

        delegate.addXP(amount);
        return this;
    }

    /**
     * Set location of the origin of the drops.
     *
     * @param location The location.
     * @return The DropQueue.
     */
    public DropQueue setLocation(@NotNull final Location location) {
        if (delegate == null) {
            return this;
        }

        delegate.setLocation(location);
        return this;
    }

    /**
     * Force the queue to act as if player is telekinetic.
     *
     * @return The DropQueue.
     */
    public DropQueue forceTelekinesis() {
        if (delegate == null) {
            return this;
        }

        delegate.forceTelekinesis();
        return this;
    }

    /**
     * Push the queue.
     */
    public void push() {
        if (delegate == null) {
            return;
        }

        delegate.push();
    }
}
