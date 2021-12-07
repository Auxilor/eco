package com.willfp.eco.core.drops;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.events.DropQueuePushEvent;
import com.willfp.eco.core.integrations.antigrief.AntigriefManager;
import org.bukkit.Bukkit;
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

    private Location location;
    private boolean forceTelekinesis = false;
    private final Player player;

    /**
     * The internally used {@link DropQueue}.
     */
    private final InternalDropQueue handle;

    /**
     * @param player The player.
     */
    public DropQueue(@NotNull final Player player) {
        handle = Eco.getHandler().getDropQueueFactory().create(player);
        this.player = player;
    }

    /**
     * Add item to queue.
     *
     * @param item The item to add.
     * @return The DropQueue.
     */
    public DropQueue addItem(@NotNull final ItemStack item) {
        handle.addItem(item);
        return this;
    }

    /**
     * Add multiple items to queue.
     *
     * @param itemStacks The items to add.
     * @return The DropQueue.
     */
    public DropQueue addItems(@NotNull final Collection<ItemStack> itemStacks) {
        handle.addItems(itemStacks);
        return this;
    }

    /**
     * Add xp to queue.
     *
     * @param amount The amount to add.
     * @return The DropQueue.
     */
    public DropQueue addXP(final int amount) {
        handle.addXP(amount);
        return this;
    }

    /**
     * Set location of the origin of the drops.
     *
     * @param location The location.
     * @return The DropQueue.
     */
    public DropQueue setLocation(@NotNull final Location location) {
        handle.setLocation(location);
        this.location = location;
        return this;
    }

    /**
     * Force the queue to act as if player is telekinetic.
     *
     * @return The DropQueue.
     */
    public DropQueue forceTelekinesis() {
        handle.forceTelekinesis();
        this.forceTelekinesis = true;
        return this;
    }

    /**
     * Push the queue.
     */
    public void push() {
        if (!AntigriefManager.canPickupItem(this.player, this.location)) return;
        DropQueuePushEvent event = new DropQueuePushEvent(this.player, this.handle, this, this.forceTelekinesis);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            handle.push();
        }
    }
}
