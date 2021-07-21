package com.willfp.eco.core.proxy;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

/**
 * Proxy / NMS constants.
 */
@UtilityClass
public class ProxyConstants {
    /**
     * The NMS version that the server is running on.
     */
    public static final String NMS_VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
}
