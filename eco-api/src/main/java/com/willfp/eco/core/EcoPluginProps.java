package com.willfp.eco.core;

import com.willfp.eco.core.config.interfaces.Config;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Plugin props are the arguments related to the plugin that are required on start-up.
 * Marked as internal as the props may change / update, which will cause problems if
 * you create props objects.
 *
 * @param resourceId           The ID of the plugin on polymart.
 * @param bStatsId             The ID of the plugin on bStats.
 * @param proxyPackage         The package where proxies can be found.
 * @param color                The primary color of the plugin.
 * @param supportingExtensions If the plugin should attempt to look for extensions.
 */
@ApiStatus.Internal
public record EcoPluginProps(int resourceId,
                             int bStatsId,
                             @NotNull String proxyPackage,
                             @NotNull String color,
                             boolean supportingExtensions) {
    /**
     * The parser for config props.
     */
    private static PropsParser<Config> configParser = null;

    /**
     * Load props from config.
     *
     * @param config The config.
     * @return The props.
     */
    public static EcoPluginProps fromConfig(@NotNull final Config config) {
        return configParser.parseFrom(config);
    }

    /**
     * Initialize the parser for eco.yml.
     *
     * @param parser The parser.
     */
    @ApiStatus.Internal
    public static void setConfigParser(@NotNull final PropsParser<Config> parser) {
        configParser = parser;
    }

    /**
     * Parse arguments into props for a plugin.
     *
     * @param <T> The type of source.
     */
    public interface PropsParser<T> {
        /**
         * Parse props from a given source.
         *
         * @param source The source.
         * @return The props.
         */
        EcoPluginProps parseFrom(@NotNull T source);
    }
}
