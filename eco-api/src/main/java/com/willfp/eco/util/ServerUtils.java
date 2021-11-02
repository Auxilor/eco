package com.willfp.eco.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Utilities / API methods for the server.
 */
@UtilityClass
public class ServerUtils {
    /**
     * The TPS supplier.
     */
    private Supplier<Double> tpsSupplier = null;

    /**
     * Get the current server TPS.
     *
     * @return The TPS.
     */
    public double getTps() {
        Validate.notNull(tpsSupplier, "Not initialized!");

        return tpsSupplier.get();
    }

    /**
     * Initialize the tps supplier function.
     *
     * @param function The function.
     */
    @ApiStatus.Internal
    public void initialize(@NotNull final Supplier<Double> function) {
        Validate.isTrue(tpsSupplier == null, "Already initialized!");

        tpsSupplier = function;
    }
}
