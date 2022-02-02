package com.willfp.eco.core.proxy;

import org.jetbrains.annotations.NotNull;

/**
 * Factory to make proxies.
 */
public interface ProxyFactory {
    /**
     * Get the proxy implementation.
     *
     * @param proxyClass The proxy class.
     * @param <T>        The proxy class.
     * @return The proxy implementation.
     */
    <T> @NotNull T getProxy(@NotNull Class<T> proxyClass);
}
