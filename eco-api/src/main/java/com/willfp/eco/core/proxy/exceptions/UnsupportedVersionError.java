package com.willfp.eco.core.proxy.exceptions;

import com.willfp.eco.core.proxy.ProxyConstants;

/**
 * Server running an unsupported version.
 */
public class UnsupportedVersionError extends Error {
    /**
     * Thrown if the server is running an unsupported version.
     */
    public UnsupportedVersionError() {
        super("You're running an unsupported server version: " + ProxyConstants.NMS_VERSION);
    }
}
