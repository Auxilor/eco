package com.willfp.eco.core.gui.slot.functional;

import com.willfp.eco.core.gui.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Interface to run on slot update.
 */
@FunctionalInterface
public interface SlotUpdater {
    /**
     * Performs this operation on the given arguments.
     *
     * @param player   The player.
     * @param menu     The menu.
     * @param previous The previous ItemStack.
     * @return The new ItemStack.
     */
    @Nullable
    ItemStack update(@NotNull Player player,
                     @NotNull Menu menu,
                     @NotNull ItemStack previous);
}
