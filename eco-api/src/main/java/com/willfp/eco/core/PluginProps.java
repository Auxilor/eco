package com.willfp.eco.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Plugin props are the arguments related to the plugin that are required on start-up.
 * <p>
 * This class is complex in how it works intentionally. This is done so that fields can be
 * added to the props without breaking API backwards compatibility. Thus, there is no public
 * constructor and no way to instantiate props without creating a parser.
 */
public final class PluginProps {
    /**
     * All registered parsers.
     */
    private static final Map<Class<?>, PropsParser<?>> REGISTERED_PARSERS = new HashMap<>();

    /**
     * The polymart resource ID.
     */
    @Nullable
    private Integer resourceId;

    /**
     * The bStats ID.
     */
    @Nullable
    private Integer bStatsId;

    /**
     * The proxy package.
     */
    @Nullable
    private String proxyPackage;

    /**
     * The color.
     */
    @Nullable
    private String color;

    /**
     * If extensions are supported.
     */
    @Nullable
    private Boolean supportingExtensions;

    /**
     * The environment variables.
     */
    private final Map<String, String> environment = new HashMap<>();

    /**
     * If the plugin uses reflective reload.
     */
    private boolean usesReflectiveReload = true;

    /**
     * Create new blank props.
     */
    private PluginProps() {

    }

    /**
     * Get resource ID.
     *
     * @return The resource ID.
     */
    public int getResourceId() {
        assert resourceId != null;
        return resourceId;
    }

    /**
     * Set resource ID.
     *
     * @param resourceId The resource ID.
     */
    public void setResourceId(final int resourceId) {
        this.resourceId = resourceId;
    }

    /**
     * Get bStats ID.
     *
     * @return The bStats ID.
     */
    public int getBStatsId() {
        assert bStatsId != null;
        return bStatsId;
    }

    /**
     * Set bStats ID.
     *
     * @param bStatsId The bStats ID.
     */
    public void setBStatsId(final int bStatsId) {
        this.bStatsId = bStatsId;
    }

    /**
     * Get the proxy package.
     *
     * @return The package.
     */
    @NotNull
    public String getProxyPackage() {
        assert proxyPackage != null;
        return proxyPackage;
    }

    /**
     * Set the proxy package.
     *
     * @param proxyPackage The proxy package.
     */
    public void setProxyPackage(@NotNull final String proxyPackage) {
        this.proxyPackage = proxyPackage;
    }

    /**
     * Get color.
     *
     * @return The color.
     */
    @NotNull
    public String getColor() {
        assert color != null;
        return color;
    }

    /**
     * Set the color.
     *
     * @param color The color.
     */
    public void setColor(@NotNull final String color) {
        this.color = color;
    }

    /**
     * Get if extensions are supported.
     *
     * @return If supported.
     */
    public boolean isSupportingExtensions() {
        assert supportingExtensions != null;
        return supportingExtensions;
    }

    /**
     * Set if extensions are supported.
     *
     * @param supportingExtensions If supported.
     */
    public void setSupportingExtensions(final boolean supportingExtensions) {
        this.supportingExtensions = supportingExtensions;
    }

    /**
     * Get an environment variable.
     *
     * @param name The name.
     * @return The value of the variable.
     */
    @Nullable
    public String getEnvironmentVariable(@NotNull final String name) {
        return environment.get(name);
    }

    /**
     * Set an environment variable.
     *
     * @param name  The name.
     * @param value The value.
     */
    public void setEnvironmentVariable(@NotNull final String name,
                                       @NotNull final String value) {
        environment.put(name, value);
    }

    /**
     * Set if the plugin uses reflective reload.
     *
     * @return If the plugin uses reflective reload.
     */
    public boolean isUsingReflectiveReload() {
        return usesReflectiveReload;
    }

    /**
     * Set if the plugin uses reflective reload.
     *
     * @param usesReflectiveReload If the plugin uses reflective reload.
     */
    public void setUsesReflectiveReload(final boolean usesReflectiveReload) {
        this.usesReflectiveReload = usesReflectiveReload;
    }

    /**
     * Ensure that all required props have been set.
     */
    public void validate() {
        if (
                supportingExtensions == null
                        || proxyPackage == null
                        || color == null
                        || bStatsId == null
                        || resourceId == null
        ) {
            throw new IllegalStateException("Missing required props!");
        }
    }

    /**
     * Parse props from source.
     *
     * @param source      The source.
     * @param sourceClass The source class.
     * @param <T>         The source type.
     * @return The props.
     */
    public static <T> PluginProps parse(@NotNull final T source,
                                        @NotNull final Class<? extends T> sourceClass) {
        for (Map.Entry<Class<?>, PropsParser<?>> entry : REGISTERED_PARSERS.entrySet()) {
            Class<?> clazz = entry.getKey();

            if (clazz.equals(sourceClass)) {
                @SuppressWarnings("unchecked")
                PropsParser<T> parser = (PropsParser<T>) entry.getValue();
                return parser.parseFrom(source);
            }
        }

        throw new IllegalArgumentException("No parser exists for class " + sourceClass);
    }

    /**
     * Register a parser for a type.
     *
     * @param clazz  The class.
     * @param parser The parser.
     * @param <T>    The source type.
     */
    public static <T> void registerParser(@NotNull final Class<T> clazz,
                                          @NotNull final PropsParser<T> parser) {
        REGISTERED_PARSERS.put(clazz, parser);
    }

    /**
     * Get if there is a registered parser for a class.
     *
     * @param clazz The class.
     * @return If there is a parser registered.
     */
    public static boolean hasParserFor(@NotNull final Class<?> clazz) {
        for (Class<?> test : REGISTERED_PARSERS.keySet()) {
            if (test.equals(clazz)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Create new props from known values.
     * <p>
     * Marked as internal as this method will break whenever the properties themselves
     * are updated (e.g. if a new property is added) - so to prevent any potential
     * backwards-compatibility bugs, this method cannot be invoked outside eco itself.
     *
     * @param resourceId         The ID of the plugin on polymart.
     * @param bStatsId           The ID of the plugin on bStats.
     * @param proxyPackage       The package where proxies can be found.
     * @param color              The primary color of the plugin.
     * @param supportsExtensions If the plugin should attempt to look for extensions.
     * @return The props.
     * @deprecated Moving to force the usage of eco.yml.
     */
    @Deprecated(since = "6.53.0")
    static PluginProps createSimple(final int resourceId,
                                    final int bStatsId,
                                    @NotNull final String proxyPackage,
                                    @NotNull final String color,
                                    final boolean supportsExtensions) {
        PluginProps props = new PluginProps();
        props.setResourceId(resourceId);
        props.setBStatsId(bStatsId);
        props.setProxyPackage(proxyPackage);
        props.setColor(color);
        props.setSupportingExtensions(supportsExtensions);
        return props;
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
        PluginProps parseFrom(@NotNull T source);

        /**
         * Get a new, blank props instance.
         *
         * @return Blank props.
         */
        default PluginProps getBlankProps() {
            return new PluginProps();
        }
    }
}
