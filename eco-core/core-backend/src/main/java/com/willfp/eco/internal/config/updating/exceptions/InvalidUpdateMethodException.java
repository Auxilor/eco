package com.willfp.eco.internal.config.updating.exceptions;

import org.jetbrains.annotations.NotNull;

public class InvalidUpdateMethodException extends RuntimeException {
    public InvalidUpdateMethodException(@NotNull final String message) {
        super(message);
    }
}
