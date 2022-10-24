package com.willfp.eco.core.config;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.config.interfaces.Config;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * Utilities / API methods for configs.
 */
public final class Configs {
    /**
     * Load a Config from a bukkit {@link ConfigurationSection}.
     *
     * @param config The ConfigurationSection.
     * @return The config.
     */
    @NotNull
    public static Config fromBukkit(@Nullable final ConfigurationSection config) {
        return config == null ? empty() : Eco.get().wrapConfigurationSection(config);
    }

    /**
     * Load a config from an {@link InputStream}.
     * <p>
     * Only for yaml configs.
     *
     * @param stream The InputStream.
     * @return The config.
     */
    @NotNull
    public static Config fromStream(@Nullable final InputStream stream) {
        return stream != null ? fromBukkit(YamlConfiguration.loadConfiguration(
                new InputStreamReader(stream)
        )) : empty();
    }

    /**
     * Load a config from a file.
     *
     * @param file The file.
     * @return The config.
     */
    @NotNull
    public static Config fromFile(@Nullable final File file) {
        if (file == null) {
            return empty();
        }

        int lastIndex = file.getName().lastIndexOf(".");

        if (lastIndex < 0) {
            return empty();
        }

        for (ConfigType type : ConfigType.values()) {
            if (file.getName().substring(lastIndex + 1).equalsIgnoreCase(type.getExtension())) {
                return fromFile(file, type);
            }
        }

        return empty();
    }

    /**
     * Load a config from a file.
     *
     * @param file The file.
     * @param type The type.
     * @return The config.
     */
    @NotNull
    public static Config fromFile(@Nullable final File file,
                                  @NotNull final ConfigType type) {
        if (file == null) {
            return empty();
        }

        try {
            return Eco.get().createConfig(Files.readString(file.toPath()), type);
        } catch (IOException e) {
            return empty();
        }
    }

    /**
     * Load config from map (uses {@link ConfigType#JSON}).
     *
     * @param values The values.
     * @return The config.
     */
    @NotNull
    public static Config fromMap(@NotNull final Map<String, Object> values) {
        return fromMap(values, ConfigType.JSON);
    }

    /**
     * Load config from map.
     *
     * @param values The values.
     * @param type   The type.
     * @return The config.
     */
    @NotNull
    public static Config fromMap(@NotNull final Map<String, Object> values,
                                 @NotNull final ConfigType type) {
        return Eco.get().createConfig(values, type);
    }

    /**
     * Create empty config (uses {@link ConfigType#JSON}).
     *
     * @return An empty config.
     */
    @NotNull
    public static Config empty() {
        return fromMap(new HashMap<>(), ConfigType.JSON);
    }

    /**
     * Create empty config.
     *
     * @param type The type.
     * @return An empty config.
     */
    @NotNull
    public static Config empty(@NotNull final ConfigType type) {
        return fromMap(new HashMap<>(), type);
    }

    /**
     * Load config from string.
     *
     * @param contents The contents of the config.
     * @param type     The config type.
     * @return The config.
     */
    @NotNull
    public static Config fromString(@NotNull final String contents,
                                    @NotNull final ConfigType type) {
        return Eco.get().createConfig(contents, type);
    }

    private Configs() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
