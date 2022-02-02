package com.willfp.eco.core.proxy.exceptions;

import org.jetbrains.annotations.NotNull;

/**
 * Error if the server is running an unsupported version.
 */
public class UnsupportedVersionException extends ProxyError {
    /**
     * Thrown if the server is running an unsupported NMS version.
     *
     * @param message The message to send.
     */
    public UnsupportedVersionException(@NotNull final String message) {
        super(message);
    }
}
