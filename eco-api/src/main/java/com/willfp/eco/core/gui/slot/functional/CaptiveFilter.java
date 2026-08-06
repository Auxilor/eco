package com.willfp.eco.core.gui.slot.functional;

import com.willfp.eco.core.gui.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Interface to test if a captive slot is allowed to contain an item given a player and a menu.
 */
@FunctionalInterface
public interface CaptiveFilter {
    /**
     * Get if the item is allowed to be placed in the slot.
     *
     * @param player    The player.
     * @param menu      The menu.
     * @param itemStack The item; null if the item is unknown.
     * @return If the item is allowed.
     */
    boolean isAllowed(@NotNull Player player,
                      @NotNull Menu menu,
                      @Nullable ItemStack itemStack);
}
