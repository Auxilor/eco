package com.willfp.eco.core.events;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Event called when multiple blocks will drop items.
 * (from mine_radius, drill, etc effects)
 */
public class MultiBlockDropItemEvent extends PlayerEvent implements Cancellable {
    private final Map<Block, BlockStateAndItems> blocks;
    private boolean cancelled;

    /**
     * Create a new MultiBlockDropItemEvent.
     *
     * @param player The player.
     * @param blocks The map of blocks bound to block state and items.
     */
    public MultiBlockDropItemEvent(Player player, Map<Block, BlockStateAndItems> blocks) {
        super(player);
        this.blocks = blocks;
    }

    /**
     * The block state of a block in this event.
     *
     * @param block the block
     * @return the block state
     */
    public BlockState getBlockState(Block block) {
        return blocks.get(block).blockState;
    }

    /**
     * The items dropped by a block in this event.
     *
     * @param block the block
     * @return the items dropped
     */
    public List<Item> getItems(Block block) {
        return blocks.get(block).items;
    }

    /**
     * The blocks involved in the event.
     *
     * @return the blocks
     */
    public Collection<Block> getBlocks() {
        return blocks.keySet();
    }

    /**
     * The map representing the blocks and their BlockStateAndItems
     *
     * @return the (mutable) map
     */
    public Map<Block, BlockStateAndItems> getMap() {
        return this.blocks;
    }

    /**
     * Bukkit parity.
     */
    private static final HandlerList HANDLERS = new HandlerList();

    @Override
    public @NonNull HandlerList getHandlers() {
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

    /**
     * Utility class
     *
     * @param blockState the block state
     * @param items      the items
     */
    public record BlockStateAndItems(BlockState blockState, List<Item> items) { }
}
