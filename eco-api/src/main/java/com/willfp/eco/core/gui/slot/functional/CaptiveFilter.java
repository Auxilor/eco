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
     * Get if allowed.
     *
     * @param player    The player.
     * @param menu      The menu.
     * @param itemStack The item.
     * @return If captive.
     */
    boolean isAllowed(@NotNull Player player,
                      @NotNull Menu menu,
                      @Nullable ItemStack itemStack);
}
