package com.willfp.eco.core.placeholder;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a placeholder that can be registered.
 */
public interface RegistrablePlaceholder extends Placeholder {
    /**
     * Get the plugin that owns the placeholder.
     *
     * @return The plugin.
     */
    @NotNull
    @Override
    EcoPlugin getPlugin();

    /**
     * Register the placeholder with the {@link PlaceholderManager}.
     *
     * @return This placeholder.
     */
    @NotNull
    default RegistrablePlaceholder register() {
        PlaceholderManager.registerPlaceholder(this);
        return this;
    }
}

