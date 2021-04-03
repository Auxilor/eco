package com.willfp.eco.internal.extensions;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class ExtensionMetadata {
    /**
     * The version of the extension.
     */
    @NotNull
    @Getter
    private final String version;

    /**
     * The extension's name.
     */
    @NotNull
    @Getter
    private final String name;

    /**
     * Create a new extension metadata.
     *
     * @param version The version for the extension to be.
     * @param name    The name of the extension.
     */
    public ExtensionMetadata(@NotNull final String version,
                             @NotNull final String name) {
        this.version = version;
        this.name = name;
    }
}
