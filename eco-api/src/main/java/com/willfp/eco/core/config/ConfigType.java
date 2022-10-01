package com.willfp.eco.core.config;

import org.jetbrains.annotations.NotNull;

/**
 * Config types, classified by file extension.
 */
public enum ConfigType {
    /**
     * .json config.
     */
    JSON("json"),

    /**
     * .yml config.
     */
    YAML("yml"),

    /**
     * .toml config.
     */
    TOML("toml");

    /**
     * The file extension.
     */
    private final String extension;

    ConfigType(@NotNull final String extension) {
        this.extension = extension;
    }

    /**
     * Get the file extension.
     *
     * @return The extension.
     */
    public String getExtension() {
        return extension;
    }
}
