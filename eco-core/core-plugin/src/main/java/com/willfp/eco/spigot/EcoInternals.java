package com.willfp.eco.spigot;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.proxy.AbstractProxy;
import com.willfp.eco.internal.Internals;
import com.willfp.eco.proxy.util.ProxyFactory;
import org.jetbrains.annotations.NotNull;

public class EcoInternals extends Internals {
    private final EcoSpigotPlugin plugin;

    EcoInternals(@NotNull final EcoSpigotPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public EcoSpigotPlugin getPlugin() {
        return plugin;
    }

    @Override
    @NotNull
    public <T extends AbstractProxy> T getProxy(@NotNull final EcoPlugin plugin,
                                                @NotNull final Class<T> proxyClass) {
        return new ProxyFactory<>(plugin, proxyClass).getProxy();
    }
}
