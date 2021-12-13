package com.willfp.eco.core;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Holds the instance of the eco handler for bridging between the frontend
 * and backend.
 *
 * @see Eco#getHandler()
 * @see Handler
 */
@ApiStatus.Internal
public final class Eco {
    /**
     * Instance of eco handler.
     */
    @ApiStatus.Internal
    private static Handler handler;

    /**
     * Set the handler.
     *
     * @param handler The handler.
     */
    @ApiStatus.Internal
    public static void setHandler(@NotNull final Handler handler) {
        Validate.isTrue(Eco.handler == null, "Already initialized!");

        Eco.handler = handler;
    }

    /**
     * Get the instance of the eco handler; the bridge between the api frontend
     * and the implementation backend.
     * <p>
     * <strong>Do not use the handler in your plugins!</strong> It can and will contain
     * breaking changes between minor versions and even patches, and you will create
     * compatibility issues by using the handler. All parts of the handler have been abstracted
     * into logically named API components that you can use.
     * <p>
     * Prior to version 6.12.0, the handler was considered as an API component, but it has
     * since been moved into an internal component, and in 6.17.0, the first breaking change
     * was introduced to {@link com.willfp.eco.core.config.wrapper.ConfigFactory}. This means
     * that any usages of the handler can now cause problems in your plugins.
     *
     * @return The handler.
     */
    @ApiStatus.Internal
    public static Handler getHandler() {
        return handler;
    }

    private Eco() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
