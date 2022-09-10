package com.willfp.eco.core.config.interfaces;

import com.willfp.eco.core.config.BuildableConfig;
import com.willfp.eco.core.config.ConfigType;
import com.willfp.eco.core.config.TransientConfig;
import com.willfp.eco.core.placeholder.AdditionalPlayer;
import com.willfp.eco.core.placeholder.InjectablePlaceholder;
import com.willfp.eco.core.placeholder.PlaceholderInjectable;
import com.willfp.eco.util.NumberUtils;
import com.willfp.eco.util.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * All configs implement this interface.
 * <p>
 * Contains all methods that must exist in yaml and json configurations.
 */
@SuppressWarnings("unused")
public interface Config extends Cloneable, PlaceholderInjectable {
    /**
     * Clears cache.
     * <p>
     * Configs no longer have caches as they have in previous versions.
     */
    @Deprecated(since = "6.31.1", forRemoval = true)
    default void clearCache() {
        // Do nothing.
    }

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
     * Recurse config keys.
     *
     * @param found The found keys.
     * @param root  The root.
     * @return The keys.
     */
    @NotNull
    default List<String> recurseKeys(@NotNull Set<String> found,
                                     @NotNull String root) {
        return new ArrayList<>();
    }

    /**
     * Get an object from config.
     *
     * @param path The path.
     * @return The object.
     */
    @Nullable
    Object get(@NotNull String path);

    /**
     * Set an object in config.
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
     * Get a decimal value via a mathematical expression.
     *
     * @param path The key to fetch the value from.
     * @return The computed value, or 0 if not found or invalid.
     */
    default int getIntFromExpression(@NotNull String path) {
        return getIntFromExpression(path, null);
    }

    /**
     * Get a decimal value via a mathematical expression.
     *
     * @param path   The key to fetch the value from.
     * @param player The player to evaluate placeholders with respect to.
     * @return The computed value, or 0 if not found or invalid.
     */
    default int getIntFromExpression(@NotNull String path,
                                     @Nullable Player player) {
        return Double.valueOf(getDoubleFromExpression(path, player)).intValue();
    }

    /**
     * Get a decimal value via a mathematical expression.
     *
     * @param path              The key to fetch the value from.
     * @param player            The player to evaluate placeholders with respect to.
     * @param additionalPlayers The additional players to evaluate placeholders with respect to.
     * @return The computed value, or 0 if not found or invalid.
     */
    default int getIntFromExpression(@NotNull String path,
                                     @Nullable Player player,
                                     @NotNull Collection<AdditionalPlayer> additionalPlayers) {
        return Double.valueOf(getDoubleFromExpression(path, player, additionalPlayers)).intValue();
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
        return getString(path, false, StringUtils.FormatOption.WITHOUT_PLACEHOLDERS);
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
     * Get a decimal value via a mathematical expression.
     *
     * @param path The key to fetch the value from.
     * @return The computed value, or 0 if not found or invalid.
     */
    default double getDoubleFromExpression(@NotNull String path) {
        return getDoubleFromExpression(path, null);
    }

    /**
     * Get a decimal value via a mathematical expression.
     *
     * @param path   The key to fetch the value from.
     * @param player The player to evaluate placeholders with respect to.
     * @return The computed value, or 0 if not found or invalid.
     */
    default double getDoubleFromExpression(@NotNull String path,
                                           @Nullable Player player) {
        return NumberUtils.evaluateExpression(this.getString(path), player, this);
    }

    /**
     * Get a decimal value via a mathematical expression.
     *
     * @param path   The key to fetch the value from.
     * @param player The player to evaluate placeholders with respect to.
     * @param additionalPlayers The additional players to evaluate placeholders with respect to.
     * @return The computed value, or 0 if not found or invalid.
     */
    default double getDoubleFromExpression(@NotNull String path,
                                           @Nullable Player player,
                                           @NotNull Collection<AdditionalPlayer> additionalPlayers) {
        return NumberUtils.evaluateExpression(this.getString(path), player, this, additionalPlayers);
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

    @Override
    default void addInjectablePlaceholder(@NotNull Iterable<InjectablePlaceholder> placeholders) {
        // Do nothing.
    }

    @Override
    default @NotNull List<InjectablePlaceholder> getPlaceholderInjections() {
        return Collections.emptyList();
    }

    @Override
    default void clearInjectedPlaceholders() {
        // Do nothing.
    }

    /**
     * Convert the config to a map of values.
     *
     * @return The values.
     */
    default Map<String, Object> toMap() {
        return new HashMap<>();
    }

    /**
     * Convert the config to a map of values.
     *
     * @return The values.
     */
    default ConfigurationSection toBukkit() {
        YamlConfiguration empty = new YamlConfiguration();
        empty.createSection("temp", this.toMap());
        return empty.getConfigurationSection("temp");
    }

    /**
     * Create a new config builder.
     *
     * @return The builder.
     */
    static BuildableConfig builder() {
        return new BuildableConfig();
    }
}
