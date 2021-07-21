package com.willfp.eco.core.integrations;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * An integration loader runs a runnable only if a specific plugin is present on the server.
 * <p>
 * Used by {@link com.willfp.eco.core.EcoPlugin} to load integrations.
 */
public class IntegrationLoader {
    /**
     * The lambda to be ran if the plugin is present.
     */
    private final Runnable runnable;

    /**
     * The plugin to require to load the integration.
     */
    @Getter
    private final String pluginName;

    /**
     * Create a new Integration Loader.
     *
     * @param pluginName The plugin to require.
     * @param onLoad     The lambda to be ran if the plugin is present.
     */
    public IntegrationLoader(@NotNull final String pluginName,
                             @NotNull final Runnable onLoad) {
        this.runnable = onLoad;
        this.pluginName = pluginName;
    }

    /**
     * Load the integration.
     */
    public void load() {
        runnable.run();
    }
}
