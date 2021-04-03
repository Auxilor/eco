package com.willfp.eco.util.serialization;

import org.jetbrains.annotations.NotNull;

public class NoRegisteredDeserializerException extends Exception {
    /**
     * Create new NoRegisteredDeserializerException.
     *
     * @param message The message.
     */
    public NoRegisteredDeserializerException(@NotNull final String message) {
        super(message);
    }
}
