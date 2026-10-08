package com.willfp.eco.core.actionbar;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Renders a persistent action bar for a player.
 * <p>
 * Called on the player's own thread every few ticks, so it should be cheap. The result is only
 * sent to the player when it differs from what they were last sent, or to keep it from fading.
 */
@FunctionalInterface
public interface ActionBarProvider {
    /**
     * Render the action bar for a player.
     *
     * @param player The player.
     * @return The action bar, or null if the bar is not active for the player.
     */
    @Nullable
    Component render(@NotNull Player player);
}
