package com.willfp.eco.core.extensions;

import java.util.Set;

/**
 * Internal component to manage loading and unloading extensions.
 */
public interface ExtensionLoader {
    /**
     * Load all extensions.
     */
    void loadExtensions();

    /**
     * Unload all loaded extensions.
     */
    void unloadExtensions();

    /**
     * Retrieve a set of all loaded extensions.
     *
     * @return An {@link Set<Extension>} of all loaded extensions.
     */
    Set<Extension> getLoadedExtensions();
}
