package com.willfp.eco.internal.config.updating.exceptions;

import org.jetbrains.annotations.NotNull;

public class InvalidUpdateMethodException extends RuntimeException {
    /**
     * Throws a new invalid update method exception.
     * <p>
     * Causes include:
     * Update method with parameters.
     * Update method is not static.
     *
     * @param message The error message to show.
     */
    public InvalidUpdateMethodException(@NotNull final String message) {
        super(message);
    }
}
