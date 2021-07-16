package com.willfp.eco.core.extensions;

import org.jetbrains.annotations.NotNull;

public class MalformedExtensionException extends RuntimeException {

    /**
     * Create a new MalformedExtensionException.
     * <p>
     * Potential causes include:
     * Missing or invalid extension.yml.
     * Invalid filetype.
     *
     * @param errorMessage The error message to show.
     */
    public MalformedExtensionException(@NotNull final String errorMessage) {
        super(errorMessage);
    }
}
