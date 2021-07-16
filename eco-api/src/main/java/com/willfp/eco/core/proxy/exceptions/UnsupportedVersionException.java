package com.willfp.eco.core.proxy.exceptions;

import org.jetbrains.annotations.NotNull;

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
