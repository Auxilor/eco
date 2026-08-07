package com.willfp.eco.core.extensions;

import java.util.Set;

/**
 * Manages the loading and unloading of extensions for a particular plugin.
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
     * @return A {@link Set} of all loaded {@link Extension}s.
     */
    Set<Extension> getLoadedExtensions();
}
