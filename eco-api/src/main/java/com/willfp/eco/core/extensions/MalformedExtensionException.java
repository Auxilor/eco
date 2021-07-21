package com.willfp.eco.core.extensions;

import org.jetbrains.annotations.NotNull;

/**
 * Potential causes include:
 * Missing or invalid extension.yml.
 * Invalid filetype.
 */
public class MalformedExtensionException extends RuntimeException {
    /**
     * Create a new MalformedExtensionException.
     *
     * @param errorMessage The error message to show.
     */
    public MalformedExtensionException(@NotNull final String errorMessage) {
        super(errorMessage);
    }
}
