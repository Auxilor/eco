package com.willfp.eco.core;

import com.willfp.eco.core.config.interfaces.Config;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

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
