package com.willfp.eco.internal.config.updating.exceptions;

import com.willfp.eco.internal.config.updating.EcoConfigHandler;
import org.jetbrains.annotations.NotNull;

public class InvalidUpdatableClassException extends RuntimeException {
    /**
     * Called when an updatable class is registered into an {@link EcoConfigHandler}.
     *
     * @param message The error message.
     */
    public InvalidUpdatableClassException(@NotNull final String message) {
        super(message);
    }
}
