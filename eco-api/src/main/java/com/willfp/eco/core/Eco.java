package com.willfp.eco.core;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Base class to hold the handler.
 *
 * @see Handler
 */
public final class Eco {
    /**
     * Instance of eco handler.
     */
    @ApiStatus.Internal
    private static Handler handler;

    /**
     * Set the handler.
     * @param handler The handler.
     */
    @ApiStatus.Internal
    public static void setHandler(@NotNull final Handler handler) {
        Validate.isTrue(Eco.handler == null, "Already initialized!");

        Eco.handler = handler;
    }

    /**
     * Get the instance of the eco handler.
     * <p>
     * The handler is, in essence, a way to interface between the eco-api
     * frontend module, and the eco-backend implementations.
     * <p>
     * There shouldn't really be any reason to ever use the handler
     * in your own plugins, and you are likely to break things. All parts of
     * the handler are abstracted into logically named parts of the API.
     * <p>
     * In versions of eco before 6.12.0, the handler was considered part of
     * the eco API, however it has since been moved into an internal component
     * that shouldn't be used in your plugins.
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
