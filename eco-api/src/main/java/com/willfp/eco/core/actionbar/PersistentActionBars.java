package com.willfp.eco.core.actionbar;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.EcoPlugin;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Class to manage persistent action bars.
 * <p>
 * A persistent action bar is shown to a player for as long as its provider renders one. When
 * several bars are active for the same player, the one with the highest priority shows, ties
 * going to the bar registered first.
 * <p>
 * Any other action bar message, from any plugin, pauses persistent bars for that player for a
 * few seconds, so one-off messages are never overwritten.
 */
public final class PersistentActionBars {
    /**
     * Register a persistent action bar.
     * <p>
     * The bar is registered under {@code plugin:id}, so registering the same ID for the same
     * plugin again replaces the existing bar rather than adding a second one. Every bar a plugin
     * owns is unregistered when the plugin is disabled.
     *
     * @param plugin   The plugin that owns the bar.
     * @param id       The ID of the bar, unique within the plugin.
     * @param priority The priority. Higher priorities show over lower ones.
     * @param provider The provider of the bar.
     * @return The bar.
     */
    @NotNull
    public static PersistentActionBar register(@NotNull final EcoPlugin plugin,
                                               @NotNull final String id,
                                               final int priority,
                                               @NotNull final ActionBarProvider provider) {
        return Eco.get().registerPersistentActionBar(plugin, id, priority, provider);
    }

    /**
     * Render and send a player's persistent action bar on the next tick, even if it has not
     * changed.
     *
     * @param player The player.
     */
    public static void refresh(@NotNull final Player player) {
        Eco.get().refreshPersistentActionBar(player);
    }

    private PersistentActionBars() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
