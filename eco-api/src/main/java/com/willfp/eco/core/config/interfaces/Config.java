package com.willfp.eco.core.config.interfaces;

import com.willfp.eco.core.config.ConfigType;
import com.willfp.eco.core.config.TransientConfig;
import com.willfp.eco.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * All configs implement this interface.
 * <p>
 * Contains all methods that must exist in yaml and json configurations.
 */
@SuppressWarnings("unused")
public interface Config extends Cloneable {
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
     * @return The subsection. Returns an empty section if not found.
     */
    @NotNull
    default Config getSubsection(@NotNull String path) {
        return Objects.requireNonNullElse(getSubsectionOrNull(path), new TransientConfig());
    }

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
    default int getInt(@NotNull String path) {
        return Objects.requireNonNullElse(getIntOrNull(path), 0);
    }

    /**
     * Get an integer from config with a specified default (not found) value.
     *
     * @param path The key to fetch the value from.
     * @param def  The value to default to if not found.
     * @return The found value, or the default.
     */
    default int getInt(@NotNull String path,
                       int def) {
        return Objects.requireNonNullElse(getIntOrNull(path), def);
    }

    /**
     * Get an integer from config.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or null if not found.
     */
    @Nullable
    Integer getIntOrNull(@NotNull String path);

    /**
     * Get a list of integers from config.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or a blank {@link java.util.ArrayList} if not found.
     */
    @NotNull
    default List<Integer> getInts(@NotNull String path) {
        return Objects.requireNonNullElse(getIntsOrNull(path), new ArrayList<>());
    }

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
    default boolean getBool(@NotNull String path) {
        return Objects.requireNonNullElse(getBoolOrNull(path), false);
    }

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
    default List<Boolean> getBools(@NotNull String path) {
        return Objects.requireNonNullElse(getBoolsOrNull(path), new ArrayList<>());
    }

    /**
     * Get a list of booleans from config.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or null if not found.
     */
    @Nullable
    List<Boolean> getBoolsOrNull(@NotNull String path);

    /**
     * Get a formatted string from config.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or an empty string if not found.
     */
    @NotNull
    default String getFormattedString(@NotNull String path) {
        return getString(path, true, StringUtils.FormatOption.WITH_PLACEHOLDERS);
    }

    /**
     * Get a formatted string from config.
     *
     * @param path   The key to fetch the value from.
     * @param option The format option.
     * @return The found value, or an empty string if not found.
     */
    @NotNull
    default String getFormattedString(@NotNull String path,
                                      @NotNull StringUtils.FormatOption option) {
        return getString(path, true, option);
    }

    /**
     * Get a string from config.
     * <p>
     * Not formatted.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or an empty string if not found.
     */
    @NotNull
    default String getString(@NotNull String path) {
        return getString(path, false);
    }

    /**
     * Get a string from config.
     *
     * @param path   The key to fetch the value from.
     * @param format If the string should be formatted.
     * @return The found value, or an empty string if not found.
     * @deprecated Since 6.18.0, {@link Config#getString(String)} is not formatted by default.
     */
    @Deprecated(since = "6.18.0")
    default String getString(@NotNull String path,
                             boolean format) {
        return this.getString(path, format, StringUtils.FormatOption.WITH_PLACEHOLDERS);
    }

    /**
     * Get a string from config.
     *
     * @param path   The key to fetch the value from.
     * @param option The format option.
     * @return The found value, or an empty string if not found.
     * @deprecated Use {@link Config#getFormattedString(String, StringUtils.FormatOption)} instead.
     */
    @NotNull
    @Deprecated
    default String getString(@NotNull String path,
                             @NotNull final StringUtils.FormatOption option) {
        return this.getString(path, true, option);
    }

    /**
     * Get a string from config.
     *
     * @param path   The key to fetch the value from.
     * @param format If the string should be formatted.
     * @param option The format option.
     * @return The found value, or an empty string if not found.
     */
    @NotNull
    default String getString(@NotNull String path,
                             boolean format,
                             @NotNull StringUtils.FormatOption option) {
        return Objects.requireNonNullElse(getStringOrNull(path, format, option), "");
    }

    /**
     * Get a formatted string from config.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or an empty string if not found.
     */
    @Nullable
    default String getFormattedStringOrNull(@NotNull String path) {
        return getStringOrNull(path, true, StringUtils.FormatOption.WITH_PLACEHOLDERS);
    }

    /**
     * Get a formatted string from config.
     *
     * @param path   The key to fetch the value from.
     * @param option The format option.
     * @return The found value, or an empty string if not found.
     */
    @Nullable
    default String getFormattedStringOrNull(@NotNull String path,
                                            @NotNull StringUtils.FormatOption option) {
        return getStringOrNull(path, true, option);
    }

    /**
     * Get a string from config.
     * <p>
     * Formatted by default.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or null if not found.
     */
    @Nullable
    default String getStringOrNull(@NotNull String path) {
        return getStringOrNull(path, false, StringUtils.FormatOption.WITH_PLACEHOLDERS);
    }

    /**
     * Get a string from config.
     *
     * @param path   The key to fetch the value from.
     * @param format If the string should be formatted.
     * @return The found value, or null if not found.
     * @deprecated Since 6.18.0, {@link Config#getString(String)} is not formatted by default.
     */
    @Nullable
    @Deprecated(since = "6.18.0")
    default String getStringOrNull(@NotNull String path,
                                   boolean format) {
        return this.getStringOrNull(path, format, StringUtils.FormatOption.WITH_PLACEHOLDERS);
    }

    /**
     * Get a string from config.
     *
     * @param path   The key to fetch the value from.
     * @param option The format option.
     * @return The found value, or null if not found.
     * @deprecated Use {@link Config#getFormattedString(String, StringUtils.FormatOption)} instead.
     */
    @Nullable
    @Deprecated
    default String getStringOrNull(@NotNull String path,
                                   @NotNull StringUtils.FormatOption option) {
        return this.getStringOrNull(path, true, option);
    }

    /**
     * Get a string from config.
     *
     * @param path   The key to fetch the value from.
     * @param format If the string should be formatted.
     * @param option The format option. If format is false, this will be ignored.
     * @return The found value, or null if not found.
     */
    @Nullable
    String getStringOrNull(@NotNull String path,
                           boolean format,
                           @NotNull StringUtils.FormatOption option);

    /**
     * Get a list of strings from config.
     * <p>
     * Formatted by default.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or a blank {@link java.util.ArrayList} if not found.
     */
    @NotNull
    default List<String> getFormattedStrings(@NotNull String path) {
        return getStrings(path, true, StringUtils.FormatOption.WITH_PLACEHOLDERS);
    }

    /**
     * Get a list of strings from config.
     * <p>
     * Formatted by default.
     *
     * @param path   The key to fetch the value from.
     * @param option The format option.
     * @return The found value, or a blank {@link java.util.ArrayList} if not found.
     */
    @NotNull
    default List<String> getFormattedStrings(@NotNull String path,
                                             @NotNull StringUtils.FormatOption option) {
        return getStrings(path, true, option);
    }

    /**
     * Get a list of strings from config.
     * <p>
     * Not formatted.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or a blank {@link java.util.ArrayList} if not found.
     */
    @NotNull
    default List<String> getStrings(@NotNull String path) {
        return getStrings(path, false, StringUtils.FormatOption.WITH_PLACEHOLDERS);
    }

    /**
     * Get a list of strings from config.
     *
     * @param path   The key to fetch the value from.
     * @param format If the strings should be formatted.
     * @return The found value, or a blank {@link java.util.ArrayList} if not found.
     * @deprecated Since 6.18.0, {@link Config#getString(String)} is not formatted by default.
     */
    @NotNull
    @Deprecated(since = "6.18.0")
    default List<String> getStrings(@NotNull String path,
                                    boolean format) {
        return this.getStrings(path, format, StringUtils.FormatOption.WITH_PLACEHOLDERS);
    }

    /**
     * Get a list of strings from config.
     *
     * @param path   The key to fetch the value from.
     * @param option The format option.
     * @return The found value, or a blank {@link java.util.ArrayList} if not found.
     * @deprecated Use {@link Config#getFormattedStrings(String, StringUtils.FormatOption)} instead.
     */
    @NotNull
    @Deprecated
    default List<String> getStrings(@NotNull String path,
                                    @NotNull StringUtils.FormatOption option) {
        return getStrings(path, false, option);
    }

    /**
     * Get a list of strings from config.
     *
     * @param path   The key to fetch the value from.
     * @param format If the strings should be formatted.
     * @param option The option.
     * @return The found value, or a blank {@link java.util.ArrayList} if not found.
     */
    @NotNull
    default List<String> getStrings(@NotNull String path,
                                    boolean format,
                                    @NotNull StringUtils.FormatOption option) {
        return Objects.requireNonNullElse(getStringsOrNull(path, format, option), new ArrayList<>());
    }

    /**
     * Get a list of strings from config.
     * <p>
     * Formatted.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or null if not found.
     */
    @Nullable
    default List<String> getFormattedStringsOrNull(@NotNull String path) {
        return getStringsOrNull(path, true, StringUtils.FormatOption.WITH_PLACEHOLDERS);
    }

    /**
     * Get a list of strings from config.
     * <p>
     * Formatted.
     *
     * @param path   The key to fetch the value from.
     * @param option The format option.
     * @return The found value, or null if not found.
     */
    @Nullable
    default List<String> getFormattedStringsOrNull(@NotNull String path,
                                                   @NotNull StringUtils.FormatOption option) {
        return getStringsOrNull(path, true, option);
    }

    /**
     * Get a list of strings from config.
     * <p>
     * Not formatted.
     * <p>
     * This will be changed in newer versions to <b>not</b> format by default.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or null if not found.
     */
    @Nullable
    default List<String> getStringsOrNull(@NotNull String path) {
        return getStringsOrNull(path, false, StringUtils.FormatOption.WITH_PLACEHOLDERS);
    }

    /**
     * Get a list of strings from config.
     *
     * @param path   The key to fetch the value from.
     * @param format If the strings should be formatted.
     * @return The found value, or null if not found.
     * @deprecated Since 6.18.0, {@link Config#getString(String)} is not formatted by default.
     */
    @Nullable
    @Deprecated(since = "6.18.0")
    default List<String> getStringsOrNull(@NotNull String path,
                                          boolean format) {
        return getStringsOrNull(path, format, StringUtils.FormatOption.WITH_PLACEHOLDERS);
    }

    /**
     * Get a list of strings from config.
     *
     * @param path   The key to fetch the value from.
     * @param option The format option.
     * @return The found value, or null if not found.
     * @deprecated Use {@link Config#getFormattedStringsOrNull(String, StringUtils.FormatOption)} instead.
     */
    @Nullable
    @Deprecated
    default List<String> getStringsOrNull(@NotNull String path,
                                          @NotNull StringUtils.FormatOption option) {
        return getStringsOrNull(path, false, option);
    }

    /**
     * Get a list of strings from config.
     *
     * @param path   The key to fetch the value from.
     * @param format If the strings should be formatted.
     * @param option The format option.
     * @return The found value, or null if not found.
     */
    @Nullable
    List<String> getStringsOrNull(@NotNull String path,
                                  boolean format,
                                  @NotNull StringUtils.FormatOption option);

    /**
     * Get a decimal from config.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or 0 if not found.
     */
    default double getDouble(@NotNull String path) {
        return Objects.requireNonNullElse(getDoubleOrNull(path), 0.0);
    }

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
    default List<Double> getDoubles(@NotNull String path) {
        return Objects.requireNonNullElse(getDoublesOrNull(path), new ArrayList<>());
    }

    /**
     * Get a list of decimals from config.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or null if not found.
     */
    @Nullable
    List<Double> getDoublesOrNull(@NotNull String path);

    /**
     * Get a list of subsections from config.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or a blank {@link java.util.ArrayList} if not found.
     */
    @NotNull
    default List<? extends Config> getSubsections(@NotNull String path) {
        return Objects.requireNonNullElse(getSubsectionsOrNull(path), new ArrayList<>());
    }

    /**
     * Get a list of subsections from config.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or null if not found.
     */
    @Nullable
    List<? extends Config> getSubsectionsOrNull(@NotNull String path);

    /**
     * Get config type.
     *
     * @return The type.
     */
    @NotNull
    ConfigType getType();

    /**
     * Clone the config.
     *
     * @return The clone.
     */
    Config clone();
}
