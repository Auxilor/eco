package com.willfp.eco.util;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Utilities / API methods for the server.
 */
public final class ServerUtils {
    /**
     * The TPS supplier.
     */
    private static Supplier<Double> tpsSupplier = null;

    /**
     * Get the current server TPS.
     *
     * @return The TPS.
     */
    public static double getTps() {
        Validate.notNull(tpsSupplier, "Not initialized!");

        double tps = tpsSupplier.get();

        if (tps > 20) {
            return 20;
        } else {
            return tps;
        }
    }

    /**
     * Initialize the tps supplier function.
     *
     * @param function The function.
     */
    @ApiStatus.Internal
    public static void initialize(@NotNull final Supplier<Double> function) {
        Validate.isTrue(tpsSupplier == null, "Already initialized!");

        tpsSupplier = function;
    }

    private ServerUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
