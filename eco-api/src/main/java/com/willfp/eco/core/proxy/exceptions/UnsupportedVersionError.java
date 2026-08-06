package com.willfp.eco.core.proxy.exceptions;

import com.willfp.eco.core.proxy.ProxyConstants;

/**
 * Server running an unsupported version.
 */
public class UnsupportedVersionError extends Error {
    /**
     * Create a new unsupported version error, thrown if the server is running a version
     * that eco has no proxy implementation for.
     * <p>
     * The message includes {@link ProxyConstants#NMS_VERSION}.
     */
    public UnsupportedVersionError() {
        super("You're running an unsupported server version: " + ProxyConstants.NMS_VERSION);
    }
}
