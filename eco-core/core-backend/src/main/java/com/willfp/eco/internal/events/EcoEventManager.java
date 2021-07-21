package com.willfp.eco.internal.events;

import com.willfp.eco.core.events.EventManager;
import com.willfp.eco.core.PluginDependent;
import com.willfp.eco.core.EcoPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class EcoEventManager extends PluginDependent<EcoPlugin> implements EventManager {
    @ApiStatus.Internal
    public EcoEventManager(@NotNull final EcoPlugin plugin) {
        super(plugin);
    }

    @Override
    public void registerListener(@NotNull final Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this.getPlugin());
    }

    @Override
    public void unregisterListener(@NotNull final Listener listener) {
        HandlerList.unregisterAll(listener);
    }

    @Override
    public void unregisterAllListeners() {
        HandlerList.unregisterAll(this.getPlugin());
    }
}
