package com.willfp.eco.core.proxy.exceptions;

import org.jetbrains.annotations.NotNull;

/**
 * Generic error with proxy loading.
 */
public class ProxyError extends RuntimeException {
    /**
     * Thrown if there is an error getting a proxy.
     *
     * @param message The message to send.
     */
    public ProxyError(@NotNull final String message) {
        super(message);
    }
}
