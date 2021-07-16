package com.willfp.eco.core;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class Eco {
    /**
     * Instance of eco handler.
     * <p>
     * The handler is, in essence, a way to interface between the eco-api
     * frontend module, and the eco-backend implementations.
     * <p>
     * There shouldn't really be any reason to ever use the handler
     * in your own plugins, but if you want to then you can - it's
     * just a part of the API like any other.
     */
    @Getter
    private Handler handler;

    /**
     * Set the handler.
     *
     * @param handler The handler.
     */
    @ApiStatus.Internal
    public void setHandler(@NotNull final Handler handler) {
        Validate.isTrue(Eco.handler == null, "Already initialized!");

        Eco.handler = handler;
    }
}
