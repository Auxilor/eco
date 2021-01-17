package com.willfp.eco.util.proxy;

import com.willfp.eco.util.internal.PluginDependent;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import org.jetbrains.annotations.NotNull;

public class ProxyFactoryFactory extends PluginDependent {
    /**
     * Pass an {@link AbstractEcoPlugin} in order to interface with it.
     *
     * @param plugin The plugin to manage.
     */
    public ProxyFactoryFactory(@NotNull final AbstractEcoPlugin plugin) {
        super(plugin);
    }

    /**
     * Get proxy factory.
     *
     * @param proxyClass The proxy class.
     * @param <T>        The class.
     * @return The factory.
     */
    public <T extends AbstractProxy> ProxyFactory<T> getFactory(@NotNull final Class<T> proxyClass) {
        return new ProxyFactory<>(this.getPlugin(), proxyClass);
    }
}
