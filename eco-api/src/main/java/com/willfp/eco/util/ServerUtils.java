package com.willfp.eco.util;

import com.willfp.eco.core.Eco;

/**
 * Utilities / API methods for the server.
 */
public final class ServerUtils {
    /**
     * Get the current server TPS.
     *
     * @return The TPS.
     */
    public static double getTps() {
        double tps = Eco.get().getTPS();

        if (tps > 20) {
            return 20;
        } else {
            return tps;
        }
    }

    private ServerUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
