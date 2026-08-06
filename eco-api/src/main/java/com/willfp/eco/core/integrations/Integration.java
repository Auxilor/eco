package com.willfp.eco.core.integrations;

import com.willfp.eco.core.registry.Registrable;
import com.willfp.eco.core.registry.Registry;
import org.jetbrains.annotations.NotNull;

/**
 * An integration with a third-party plugin.
 * <p>
 * Integrations are registered into an {@link IntegrationRegistry} by their manager class,
 * and are identified by the name of the plugin that they hook into.
 */
public interface Integration extends Registrable {
    /**
     * Get the name of the plugin that this integration hooks into.
     *
     * @return The plugin name.
     */
    String getPluginName();

    @Override
    default @NotNull String getID() {
        return Registry.tryFitPattern(this.getPluginName());
    }
}
