package com.willfp.eco.core.gui.menu;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Interface to run on menu open.
 */
@FunctionalInterface
public interface OpenHandler {
    /**
     * Performs this operation on the given arguments.
     *
     * @param player The player.
     * @param menu   The menu.
     */
    void handle(@NotNull Player player,
                @NotNull Menu menu);
}
