package com.willfp.eco.util.proxy;

import com.willfp.eco.util.internal.PluginDependent;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class ProxyFactoryFactory extends PluginDependent {
    /**
     * Cached proxy implementations in order to not perform expensive reflective class-finding.
     */
    private final Map<Class<? extends AbstractProxy>, ProxyFactory<? extends AbstractProxy>> cache = new HashMap<>();

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
        ProxyFactory<T> cached = getCached(proxyClass);
        if (cached == null) {
            cache(proxyClass, new ProxyFactory<>(this.getPlugin(), proxyClass));
        } else {
            return cached;
        }
        return getFactory(proxyClass);
    }

    /**
     * Cache proxy factory.
     *
     * @param proxyClass The class.
     * @param factory    The factory.
     */
    public void cache(@NotNull final Class<? extends AbstractProxy> proxyClass,
                      @NotNull final ProxyFactory<? extends AbstractProxy> factory) {
        cache.put(proxyClass, factory);
    }

    /**
     * Get cached proxy factory.
     *
     * @param proxyClass The class.
     * @param <T>        The type of proxy.
     * @return The factory.
     */
    @Nullable
    public <T extends AbstractProxy> ProxyFactory<T> getCached(@NotNull final Class<T> proxyClass) {
        return (ProxyFactory<T>) cache.get(proxyClass);
    }
}
