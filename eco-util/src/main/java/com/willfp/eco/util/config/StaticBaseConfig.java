package com.willfp.eco.util.config;

import com.willfp.eco.util.StringUtils;
import com.willfp.eco.util.internal.PluginDependent;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.Objects;

public abstract class StaticBaseConfig extends PluginDependent {
    /**
     * The linked {@link YamlConfiguration} where values are physically stored.
     */
    @Getter(AccessLevel.PUBLIC)
    protected final YamlConfiguration config;

    /**
     * The physical config file, as stored on disk.
     */
    @Getter(AccessLevel.PROTECTED)
    private final File configFile;

    /**
     * The full name of the config file (eg config.yml).
     */
    @Getter(AccessLevel.PROTECTED)
    private final String name;

    /**
     * Config implementation for configs present in the plugin's base directory (eg config.yml, lang.yml).
     * <p>
     * Does not automatically update.
     *
     * @param configName The name of the config
     * @param plugin     The plugin.
     */
    protected StaticBaseConfig(@NotNull final String configName,
                               @NotNull final AbstractEcoPlugin plugin) {
        super(plugin);
        this.name = configName + ".yml";

        if (!new File(this.getPlugin().getDataFolder(), this.name).exists()) {
            createFile();
        }

        this.configFile = new File(this.getPlugin().getDataFolder(), this.name);
        this.config = YamlConfiguration.loadConfiguration(configFile);
    }

    private void createFile() {
        this.getPlugin().saveResource(name, false);
    }

    /**
     * Get if the config contains a key.
     *
     * @param path The key to check.
     * @return If contained.
     */
    public boolean has(@NotNull final String path) {
        return config.contains(path);
    }

    /**
     * Get configuration section from config.
     *
     * @param path The key to check.
     * @return The configuration section. Throws NPE if not found.
     */
    @NotNull
    public ConfigurationSection getSection(@NotNull final String path) {
        ConfigurationSection section = getSectionOrNull(path);
        if (section == null) {
            throw new NullPointerException("Section cannot be null!");
        } else {
            return section;
        }
    }

    /**
     * Get configuration section from config.
     *
     * @param path The key to check.
     * @return The configuration section, or null if not found.
     */
    @Nullable
    public ConfigurationSection getSectionOrNull(@NotNull final String path) {
        return config.getConfigurationSection(path);
    }

    /**
     * Get an integer from config.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or 0 if not found.
     */
    public int getInt(@NotNull final String path) {
        return config.getInt(path, 0);
    }

    /**
     * Get an integer from config.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or null if not found.
     */
    @Nullable
    public Integer getIntOrNull(@NotNull final String path) {
        if (has(path)) {
            return getInt(path);
        } else {
            return null;
        }
    }

    /**
     * Get an integer from config with a specified default (not found) value.
     *
     * @param path The key to fetch the value from.
     * @param def  The value to default to if not found.
     * @return The found value, or the default.
     */
    public int getInt(@NotNull final String path,
                      final int def) {
        return config.getInt(path, def);
    }

    /**
     * Get a list of integers from config.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or a blank {@link java.util.ArrayList} if not found.
     */
    @NotNull
    public List<Integer> getInts(@NotNull final String path) {
        return config.getIntegerList(path);
    }

    /**
     * Get a list of integers from config.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or null if not found.
     */
    @Nullable
    public List<Integer> getIntsOrNull(@NotNull final String path) {
        if (has(path)) {
            return getInts(path);
        } else {
            return null;
        }
    }

    /**
     * Get a boolean from config.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or false if not found.
     */
    public boolean getBool(@NotNull final String path) {
        return config.getBoolean(path, false);
    }

    /**
     * Get a boolean from config.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or null if not found.
     */
    @Nullable
    public Boolean getBoolOrNull(@NotNull final String path) {
        if (has(path)) {
            return getBool(path);
        } else {
            return null;
        }
    }

    /**
     * Get a list of booleans from config.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or a blank {@link java.util.ArrayList} if not found.
     */
    @NotNull
    public List<Boolean> getBools(@NotNull final String path) {
        return config.getBooleanList(path);
    }

    /**
     * Get a list of booleans from config.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or null if not found.
     */
    @Nullable
    public List<Boolean> getBoolsOrNull(@NotNull final String path) {
        if (has(path)) {
            return getBools(path);
        } else {
            return null;
        }
    }

    /**
     * Get a string from config.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or an empty string if not found.
     */
    @NotNull
    public String getString(@NotNull final String path) {
        return StringUtils.translate(Objects.requireNonNull(config.getString(path, "")));
    }

    /**
     * Get a string from config.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or null if not found.
     */
    @Nullable
    public String getStringOrNull(@NotNull final String path) {
        if (has(path)) {
            return getString(path);
        } else {
            return null;
        }
    }

    /**
     * Get a list of strings from config.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or a blank {@link java.util.ArrayList} if not found.
     */
    @NotNull
    public List<String> getStrings(@NotNull final String path) {
        return config.getStringList(path);
    }

    /**
     * Get a list of strings from config.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or null if not found.
     */
    @Nullable
    public List<String> getStringsOrNull(@NotNull final String path) {
        if (has(path)) {
            return getStrings(path);
        } else {
            return null;
        }
    }

    /**
     * Get a decimal from config.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or 0 if not found.
     */
    public double getDouble(@NotNull final String path) {
        return config.getDouble(path, 0);
    }

    /**
     * Get a decimal from config.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or null if not found.
     */
    @Nullable
    public Double getDoubleOrNull(@NotNull final String path) {
        if (has(path)) {
            return getDouble(path);
        } else {
            return null;
        }
    }

    /**
     * Get a list of decimals from config.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or a blank {@link java.util.ArrayList} if not found.
     */
    @NotNull
    public List<Double> getDoubles(@NotNull final String path) {
        return config.getDoubleList(path);
    }

    /**
     * Get a list of decimals from config.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or null if not found.
     */
    @Nullable
    public List<Double> getDoublesOrNull(@NotNull final String path) {
        if (has(path)) {
            return getDoubles(path);
        } else {
            return null;
        }
    }
}
