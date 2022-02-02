package com.willfp.eco.core.proxy;

import org.bukkit.Bukkit;

/**
 * Proxy / NMS constants.
 */
public final class ProxyConstants {
    /**
     * The NMS version that the server is running on.
     */
    public static final String NMS_VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

    private ProxyConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
