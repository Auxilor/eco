package com.willfp.eco.core.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Event called when multiple blocks are being broken
 * (from mine_radius, drill, etc effects)
 */
public class MultiBlockBreakEvent extends PlayerEvent implements Cancellable {
    private final Map<Block, Boolean> blocks = new HashMap<>();
    private boolean cancelled;

    /**
     * Create a new MultiBlockBreakEvent.
     *
     * @param player The player.
     * @param blocks The blocks that will be broken.
     */
    public MultiBlockBreakEvent(Player player, Collection<Block> blocks) {
        super(player);
        for (Block block : blocks)
            this.blocks.put(block, true);
    }

    /**
     * Get the blocks that will be broken.
     *
     * @return the blocks that will be broken.
     */
    public Collection<Block> getBlocks() {
        return blocks.keySet();
    }

    /**
     * Get if block is dropping items.
     *
     * @param block the block
     * @return if the block is dropping items.
     */
    public boolean isDropItems(Block block) {
        return blocks.get(block);
    }

    /**
     * Set if block is dropping items.
     *
     * @param block     the block.
     * @param dropItems if will drop the items.
     */
    public void setDropItems(Block block, boolean dropItems) {
        if (this.blocks.containsKey(block))
            this.blocks.put(block, dropItems);
    }

    /**
     * Bukkit parity.
     */
    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * Gets a list of handlers handling this event.
     *
     * @return A list of handlers handling this event.
     */
    @Override
    @NonNull
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
        return cancelled;
    }

    /**
     * Set cancel state.
     *
     * @param cancelled If cancelled.
     */
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}