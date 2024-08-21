package com.willfp.eco.core.data.handlers;

import com.willfp.eco.core.registry.Registry;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class to manage persistent data handlers.
 */
public final class PersistentDataHandlers {
    private static final Registry<PersistentDataHandler> REGISTRY = new Registry<>();

    /**
     * Register a persistent data handler.
     *
     * @param handler The handler.
     */
    public static void register(@NotNull final PersistentDataHandler handler) {
        REGISTRY.register(handler);
    }

    /**
     * Get a persistent data handler by id.
     *
     * @param id The id.
     * @return The handler.
     * @throws IllegalArgumentException if no handler with that id is found.
     */
    @NotNull
    public static PersistentDataHandler get(@NotNull final String id) {
        PersistentDataHandler handler = REGISTRY.get(id);

        if (handler == null) {
            throw new IllegalArgumentException("No handler with id: " + id);
        }

        return handler;
    }

    private PersistentDataHandlers() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
