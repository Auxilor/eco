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
