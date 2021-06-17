package com.willfp.eco.core.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Config {
    /**
     * Clears cache.
     */
    void clearCache();

    /**
     * Convert the config into readable text.
     *
     * @return The plaintext.
     */
    String toPlaintext();

    /**
     * Get if the config contains a key.
     *
     * @param path The key to check.
     * @return If contained.
     */
    boolean has(@NotNull String path);

    /**
     * Get config keys.
     *
     * @param deep If keys from subsections should be fetched too.
     * @return A list of keys.
     */
    @NotNull
    List<String> getKeys(boolean deep);

    /**
     * Get an object from config.
     * Default implementations call {@link org.bukkit.configuration.file.YamlConfiguration#get(String)}.
     *
     * @param path The path.
     * @return The object.
     */
    @Nullable
    Object get(@NotNull String path);

    /**
     * Set an object in config.
     * Default implementations call {@link org.bukkit.configuration.file.YamlConfiguration#set(String, Object)}
     *
     * @param path   The path.
     * @param object The object.
     */
    void set(@NotNull String path,
             @Nullable Object object);

    /**
     * Get subsection from config.
     *
     * @param path The key to check.
     * @return The subsection. Throws NPE if not found.
     */
    @NotNull
    Config getSubsection(@NotNull String path);

    /**
     * Get subsection from config.
     *
     * @param path The key to check.
     * @return The subsection, or null if not found.
     */
    @Nullable
    Config getSubsectionOrNull(@NotNull String path);

    /**
     * Get an integer from config.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or 0 if not found.
     */
    int getInt(@NotNull String path);

    /**
     * Get an integer from config.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or null if not found.
     */
    @Nullable
    Integer getIntOrNull(@NotNull String path);

    /**
     * Get an integer from config with a specified default (not found) value.
     *
     * @param path The key to fetch the value from.
     * @param def  The value to default to if not found.
     * @return The found value, or the default.
     */
    int getInt(@NotNull String path,
               int def);

    /**
     * Get a list of integers from config.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or a blank {@link java.util.ArrayList} if not found.
     */
    @NotNull
    List<Integer> getInts(@NotNull String path);

    /**
     * Get a list of integers from config.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or null if not found.
     */
    @Nullable
    List<Integer> getIntsOrNull(@NotNull String path);

    /**
     * Get a boolean from config.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or false if not found.
     */
    boolean getBool(@NotNull String path);

    /**
     * Get a boolean from config.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or null if not found.
     */
    @Nullable
    Boolean getBoolOrNull(@NotNull String path);

    /**
     * Get a list of booleans from config.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or a blank {@link java.util.ArrayList} if not found.
     */
    @NotNull
    List<Boolean> getBools(@NotNull String path);

    /**
     * Get a list of booleans from config.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or null if not found.
     */
    @Nullable
    List<Boolean> getBoolsOrNull(@NotNull String path);

    /**
     * Get a string from config.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or an empty string if not found.
     */
    @NotNull
    String getString(@NotNull String path);

    /**
     * Get a string from config.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or null if not found.
     */
    @Nullable
    String getStringOrNull(@NotNull String path);

    /**
     * Get a list of strings from config.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or a blank {@link java.util.ArrayList} if not found.
     */
    @NotNull
    List<String> getStrings(@NotNull String path);

    /**
     * Get a list of strings from config.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or null if not found.
     */
    @Nullable
    List<String> getStringsOrNull(@NotNull String path);

    /**
     * Get a decimal from config.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or 0 if not found.
     */
    double getDouble(@NotNull String path);

    /**
     * Get a decimal from config.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or null if not found.
     */
    @Nullable
    Double getDoubleOrNull(@NotNull String path);

    /**
     * Get a list of decimals from config.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or a blank {@link java.util.ArrayList} if not found.
     */
    @NotNull
    List<Double> getDoubles(@NotNull String path);

    /**
     * Get a list of decimals from config.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or null if not found.
     */
    @Nullable
    List<Double> getDoublesOrNull(@NotNull String path);
}
