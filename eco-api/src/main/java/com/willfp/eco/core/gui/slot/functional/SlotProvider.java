package com.willfp.eco.core.gui.slot.functional;

import com.willfp.eco.core.gui.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Interface to run on slot display.
 */
@FunctionalInterface
public interface SlotProvider {
    /**
     * Performs this operation on the given arguments.
     *
     * @param player The player.
     * @param menu   The menu.
     * @return The ItemStack.
     */
    @Nullable
    ItemStack provide(@NotNull Player player,
                      @NotNull Menu menu);
}
