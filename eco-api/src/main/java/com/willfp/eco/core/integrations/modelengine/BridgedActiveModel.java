package com.willfp.eco.core.integrations.modelengine;

import org.jetbrains.annotations.NotNull;

/**
 * A ModelEngine model instance.
 */
public interface BridgedActiveModel {
    /**
     * Get the ID of the model blueprint.
     *
     * @return The ID.
     */
    @NotNull
    String getId();

    /**
     * Get the animation handler.
     *
     * @return The animation handler.
     */
    @NotNull
    BridgedAnimationHandler getAnimationHandler();
}
