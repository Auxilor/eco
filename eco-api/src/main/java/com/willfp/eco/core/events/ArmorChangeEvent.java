package com.willfp.eco.core.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The armor change event <b>does</b> contain information about the event.
 * <p>
 * Unlike {@link ArmorEquipEvent}, it is called the next tick and contains previous and current armor contents.
 */
public class ArmorChangeEvent extends PlayerEvent {
    /**
     * Bukkit parity.
     */
    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * The armor contents before. 0 is helmet, 3 is boots.
     */
    private final List<ItemStack> before;

    /**
     * The armor contents after. 0 is helmet, 3 is boots.
     */
    private final List<ItemStack> after;

    /**
     * Create a new ArmorChangeEvent.
     *
     * @param player The player.
     * @param before The armor contents before.
     * @param after  The armor contents after.
     */
    public ArmorChangeEvent(@NotNull final Player player,
                            @NotNull final List<ItemStack> before,
                            @NotNull final List<ItemStack> after) {
        super(player);
        this.before = before;
        this.after = after;
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
     * Get the contents before the change.
     *
     * @return The contents.
     */
    public List<ItemStack> getBefore() {
        return this.before;
    }

    /**
     * Get the current contents.
     *
     * @return The contents.
     */
    public List<ItemStack> getAfter() {
        return this.after;
    }
}
