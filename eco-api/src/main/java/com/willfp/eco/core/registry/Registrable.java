package com.willfp.eco.core.registry;

import org.jetbrains.annotations.NotNull;

/**
 * An object that can be registered.
 *
 * @see Registry
 */
public interface Registrable {
    /**
     * Get the ID of the element.
     *
     * @return The ID.
     */
    @NotNull
    String getID();

    /**
     * Called when the element is registered.
     */
    default void onRegister() {
        // Do nothing by default.
    }

    /**
     * Called when the element is removed.
     */
    default void onRemove() {
        // Do nothing by default.
    }
}
