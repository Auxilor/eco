package com.willfp.eco.core.extensions;

import org.jetbrains.annotations.NotNull;

/**
 * Thrown when an extension jar is malformed and therefore cannot be loaded.
 * <p>
 * Potential causes include:
 * <ul>
 *     <li>Missing or invalid extension.yml.</li>
 *     <li>Invalid filetype.</li>
 * </ul>
 */
public class MalformedExtensionException extends ExtensionLoadException {
    /**
     * Create a new MalformedExtensionException.
     *
     * @param errorMessage The error message to show.
     */
    public MalformedExtensionException(@NotNull final String errorMessage) {
        super(errorMessage);
    }
}
