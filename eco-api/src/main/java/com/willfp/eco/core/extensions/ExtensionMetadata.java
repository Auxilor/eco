package com.willfp.eco.core.extensions;

import com.willfp.eco.core.version.Version;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * The extension's metadata.
 * <p>
 * Stored as a record internally.
 *
 * @param version The extension version.
 * @param name    The extension name.
 * @param author  The extension's author.
 * @param file    The extension's file.
 * @param minimumPluginVersion The minimum plugin version required for this extension.
 */
public record ExtensionMetadata(@NotNull String version,
                                @NotNull String name,
                                @NotNull String author,
                                @NotNull File file,
                                @NotNull Version minimumPluginVersion) {
}
