package com.willfp.eco.core.version;

import org.jetbrains.annotations.NotNull;

/**
 * An error thrown when eco is outdated.
 */
public class OutdatedEcoVersionError extends Error {
    /**
     * Create a new OutdatedEcoVersionError.
     *
     * @param message The message.
     */
    public OutdatedEcoVersionError(@NotNull final String message) {
        super(message);
    }
}
