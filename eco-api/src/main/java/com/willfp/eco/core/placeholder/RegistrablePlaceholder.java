package com.willfp.eco.core.placeholder;

import com.willfp.eco.core.integrations.placeholder.PlaceholderManager;
import org.jetbrains.annotations.NotNull;

public interface RegistrablePlaceholder extends Placeholder {
    /**
     * Register the arguments.
     *
     * @return The arguments.
     */
    @NotNull
    default RegistrablePlaceholder register() {
        PlaceholderManager.registerPlaceholder(this);
        return this;
    }
}

