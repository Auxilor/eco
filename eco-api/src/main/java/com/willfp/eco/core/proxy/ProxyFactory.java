package com.willfp.eco.core.proxy;

import org.jetbrains.annotations.NotNull;

/**
 * Factory to make proxies.
 * <p>
 * Each plugin that declares a proxy package gets its own factory, which resolves proxy
 * interfaces to the implementation for the running server version.
 */
public interface ProxyFactory {
    /**
     * Get the proxy implementation.
     *
     * @param proxyClass The proxy class.
     * @param <T>        The proxy type.
     * @return The proxy implementation.
     */
    <T> @NotNull T getProxy(@NotNull Class<T> proxyClass);
}
