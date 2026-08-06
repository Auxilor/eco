package com.willfp.eco.core.events;

import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * The armor change event <b>does</b> contain information about the event.
 * <p>
 * Unlike {@link ArmorEquipEvent}, it is called the next tick and contains previous and current armor contents.
 * <p>
 * It is called a tick after an {@link ArmorEquipEvent}, by which point the player's inventory
 * has updated, so {@link #getAfter()} reflects the new armor.
 * <p>
 * The event is not cancellable, as the armor has already changed by the time it is called.
 */
public class ArmorChangeEvent extends PlayerEvent {
    /**
     * Bukkit parity.
     */
    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * The armor contents before, in armor slot order: 0 is boots, 3 is helmet.
     */
    private final List<ItemStack> before;

    /**
     * The armor contents after, in armor slot order: 0 is boots, 3 is helmet.
     */
    private final List<ItemStack> after;

    /**
     * Create a new ArmorChangeEvent.
     *
     * @param player The player.
     * @param before The armor contents before, in armor slot order (0 is boots, 3 is helmet).
     * @param after  The armor contents after, in armor slot order (0 is boots, 3 is helmet).
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
     * @return The contents, in armor slot order (0 is boots, 3 is helmet).
     */
    public List<ItemStack> getBefore() {
        return this.before;
    }

    /**
     * Get the current contents.
     *
     * @return The contents, in armor slot order (0 is boots, 3 is helmet).
     */
    public List<ItemStack> getAfter() {
        return this.after;
    }
}
