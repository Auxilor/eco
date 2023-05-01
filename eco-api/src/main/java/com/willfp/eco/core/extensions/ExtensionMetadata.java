package com.willfp.eco.core.extensions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * The extension's metadata.
 * <p>
 * Stored as a record internally.
 *
 * @param version The extension version.
 * @param name    The extension name.
 * @param author  The extension's author.
 */
public record ExtensionMetadata(@NotNull String version,
                                @NotNull String name,
                                @NotNull String author,
                                @Nullable File file) {
    /**
     * Legacy constructor.
     *
     * @param version The extension version.
     * @param name    The extension name.
     * @param author  The extension's author.
     * @deprecated Use {@link ExtensionMetadata#ExtensionMetadata(String, String, String, File)} instead.
     */
    @Deprecated(since = "6.57.0", forRemoval = true)
    public ExtensionMetadata(@NotNull String version,
                             @NotNull String name,
                             @NotNull String author) {
        this(version, name, author, null);
    }
}
