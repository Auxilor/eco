package com.willfp.eco.core.integrations.modelengine;

import org.jetbrains.annotations.NotNull;

/**
 * A ModelEngine animation.
 */
public interface BridgedAnimation {
    /**
     * Get the name of the animation.
     *
     * @return The name.
     */
    @NotNull
    String getName();
}
