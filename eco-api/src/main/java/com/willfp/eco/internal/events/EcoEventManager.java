package com.willfp.eco.internal.events;

import com.willfp.eco.core.events.EventManager;
import com.willfp.eco.core.PluginDependent;
import com.willfp.eco.core.EcoPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class EcoEventManager extends PluginDependent implements EventManager {
    /**
     * Manager class for event management.
     *
     * @param plugin The {@link EcoPlugin} that this manages the events of.
     */
    @ApiStatus.Internal
    public EcoEventManager(@NotNull final EcoPlugin plugin) {
        super(plugin);
    }

    /**
     * Register a listener with bukkit.
     *
     * @param listener The listener to register.
     */
    @Override
    public void registerListener(@NotNull final Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this.getPlugin());
    }

    /**
     * Unregister a listener with bukkit.
     *
     * @param listener The listener to unregister.
     */
    @Override
    public void unregisterListener(@NotNull final Listener listener) {
        HandlerList.unregisterAll(listener);
    }

    /**
     * Unregister all listeners associated with the plugin.
     */
    @Override
    public void unregisterAllListeners() {
        HandlerList.unregisterAll(this.getPlugin());
    }
}
