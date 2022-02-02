package com.willfp.eco.core.extensions;

import org.jetbrains.annotations.NotNull;

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
                                @NotNull String author) {

}
