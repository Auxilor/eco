package com.willfp.eco.util.proxy;

import com.willfp.eco.util.internal.PluginDependent;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import org.jetbrains.annotations.NotNull;

public class ProxyFactory<T extends AbstractProxy> extends PluginDependent {
    /**
     * The instance of the proxy.
     */
    private final T instance;

    /**
     * Create a new Proxy Factory for a specific type.
     *
     * @param plugin     The plugin to create proxies for.
     * @param proxyClass The class of the proxy interface.
     */
    public ProxyFactory(@NotNull final AbstractEcoPlugin plugin,
                        @NotNull final Class<T> proxyClass) {
        super(plugin);

        try {
            String className = this.getPlugin().getProxyPackage() + "." + ProxyConstants.NMS_VERSION + "." + proxyClass.getSimpleName().replace("Proxy", "");
            final Class<?> class2 = Class.forName(className);
            Object instance = class2.getConstructor().newInstance();

            if (proxyClass.isInstance(instance)) {
                this.instance = proxyClass.cast(instance);
            } else {
                throw new UnsupportedVersionException("You're running an unsupported server version: " + ProxyConstants.NMS_VERSION);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new UnsupportedVersionException("You're running an unsupported server version: " + ProxyConstants.NMS_VERSION);
        }
    }

    /**
     * Get the implementation of a proxy.
     *
     * @return The proxy implementation.
     */
    public @NotNull T getProxy() {
        return instance;
    }
}
