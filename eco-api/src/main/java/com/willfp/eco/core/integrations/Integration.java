package com.willfp.eco.core.integrations;

import com.willfp.eco.core.registry.Registrable;
import com.willfp.eco.core.registry.Registry;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract class for integrations.
 */
public interface Integration extends Registrable {
    /**
     * Get the name of integration.
     *
     * @return The name.
     */
    String getPluginName();

    @Override
    default @NotNull String getID() {
        return Registry.tryFitPattern(this.getPluginName());
    }
}
