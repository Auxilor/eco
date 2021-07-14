package com.willfp.eco.core.proxy;

import org.jetbrains.annotations.NotNull;

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
