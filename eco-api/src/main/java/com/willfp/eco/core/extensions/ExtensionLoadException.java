package com.willfp.eco.core.extensions;

import org.jetbrains.annotations.NotNull;

/**
 * Generic exception in extension loading.
 */
public class ExtensionLoadException extends RuntimeException {
    /**
     * Create a new ExtensionLoadException.
     *
     * @param errorMessage The error message to show.
     */
    public ExtensionLoadException(@NotNull final String errorMessage) {
        super(errorMessage);
    }
}
