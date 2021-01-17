package com.willfp.eco.util.proxy;

import com.willfp.eco.util.internal.PluginDependent;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.IdentityHashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class ProxyFactoryFactory extends PluginDependent {
    /**
     * Cached proxy implementations in order to not perform expensive reflective class-finding.
     */
    private final Map<Class<? extends AbstractProxy>, ProxyFactory<? extends AbstractProxy>> cache = new IdentityHashMap<>();

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
        ProxyFactory<T> cached = (ProxyFactory<T>) cache.get(proxyClass);
        if (cached == null) {
            cache.put(proxyClass, new ProxyFactory<>(this.getPlugin(), proxyClass));
        } else {
            return cached;
        }
        return getFactory(proxyClass);
    }
}
