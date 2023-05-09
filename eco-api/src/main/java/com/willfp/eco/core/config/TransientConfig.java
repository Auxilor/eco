package com.willfp.eco.core.config;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.config.wrapper.ConfigWrapper;
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
 * Config that exists purely in the code, not linked to any file.
 * <p>
 * Use for inline configs to move data around or to add subsections to other configs.
 *
 * @deprecated Poorly named class, makes the config system seem needlessly complicated.
 */
@Deprecated(since = "6.44.0", forRemoval = true)
public class TransientConfig extends ConfigWrapper<Config> {
    /**
     * Create new transient config from bukkit config.
     *
     * @param config The ConfigurationSection handle.
     */
    public TransientConfig(@NotNull final ConfigurationSection config) {
        super(Eco.get().wrapConfigurationSection(config));
    }

    /**
     * Exists for backwards compatibility, YamlConfigurations are ConfigurationSections.
     *
     * @param config The YamlConfiguration handle.
     */
    public TransientConfig(@NotNull final YamlConfiguration config) {
        this((ConfigurationSection) config);
    }

    /**
     * Create a transient config from an input stream.
     *
     * @param stream The InputStream.
     */
    public TransientConfig(@Nullable final InputStream stream) {
        super(stream != null ? Eco.get().wrapConfigurationSection(YamlConfiguration.loadConfiguration(
                new InputStreamReader(stream)
        )) : new TransientConfig());
    }

    /**
     * Load a file to a config.
     *
     * @param file The file.
     * @param type The config type to try read from.
     */
    public TransientConfig(@Nullable final File file,
                           @NotNull final ConfigType type) {
        super(file != null ? Eco.get().createConfig(readFile(file), type)
                : new TransientConfig());
    }

    /**
     * Create a new empty transient config.
     *
     * @param values The values.
     */
    public TransientConfig(@NotNull final Map<String, Object> values) {
        super(Eco.get().createConfig(values, ConfigType.YAML));
    }

    /**
     * Create a new empty transient config.
     *
     * @param values The values.
     * @param type   The type.
     */
    public TransientConfig(@NotNull final Map<String, Object> values,
                           @NotNull final ConfigType type) {
        super(Eco.get().createConfig(values, type));
    }

    /**
     * Create a new empty transient config.
     */
    public TransientConfig() {
        this(new HashMap<>(), ConfigType.JSON);
    }

    /**
     * Load a config from a string.
     *
     * @param contents The contents of the config.
     * @param type     The config type.
     */
    public TransientConfig(@NotNull final String contents,
                           @NotNull final ConfigType type) {
        super(Eco.get().createConfig(contents, type));
    }

    /**
     * Read a file to a string.
     *
     * @param file The file.
     * @return The string.
     */
    private static String readFile(@Nullable final File file) {
        if (file == null) {
            return "";
        }

        try {
            return Files.readString(file.toPath());
        } catch (IOException e) {
            return "";
        }
    }
}
