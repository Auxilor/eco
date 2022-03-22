package com.willfp.eco.core.proxy.exceptions;

import org.jetbrains.annotations.NotNull;

/**
 * Generic error with proxy loading.
 */
public class ProxyError extends Error {
    /**
     * Thrown if there is an error getting a proxy.
     *
     * @param message The message to send.
     * @param cause   The cause.
     */
    public ProxyError(@NotNull final String message,
                      @NotNull final Throwable cause) {
        super(message, cause);
    }

    /**
     * Thrown if there is an error getting a proxy.
     *
     * @param message The message to send.
     * @deprecated Proxy Errors should include a cause.
     */
    @Deprecated(forRemoval = true)
    public ProxyError(@NotNull final String message) {
        super(message);
    }
}
