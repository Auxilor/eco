package com.willfp.eco.core.proxy.exceptions;

import com.willfp.eco.core.proxy.ProxyConstants;
import org.jetbrains.annotations.NotNull;

/**
 * Error if the server is running an unsupported version.
 *
 * @deprecated Poorly named, exception when it's actually an error, contains doubly nested errors.
 */
@SuppressWarnings("removal")
@Deprecated(since = "6.24.0", forRemoval = true)
public class UnsupportedVersionException extends ProxyError {
    /**
     * Thrown if the server is running an unsupported NMS version.
     *
     * @param message The message to send.
     * @deprecated Use the default constructor.
     */
    @Deprecated(since = "6.24.0", forRemoval = true)
    public UnsupportedVersionException(@NotNull final String message) {
        super(message);
    }

    /**
     * Thrown if the server is running an unsupported NMS version.
     */
    public UnsupportedVersionException() {
        super("You're running an unsupported server version: " + ProxyConstants.NMS_VERSION, new IllegalStateException());
    }
}
