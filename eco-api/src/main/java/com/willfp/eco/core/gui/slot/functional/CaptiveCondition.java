package com.willfp.eco.core.gui.slot.functional;

import com.willfp.eco.core.gui.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Interface to test if a captive slot is captive given a player, menu, and item.
 */
@FunctionalInterface
public interface CaptiveCondition {
    /**
     * Get if the slot is captive.
     *
     * @param player    The player.
     * @param menu      The menu.
     * @param itemStack The item.
     * @return If captive.
     */
    boolean isCaptive(@NotNull Player player,
                      @NotNull Menu menu,
                      @Nullable ItemStack itemStack);
}
