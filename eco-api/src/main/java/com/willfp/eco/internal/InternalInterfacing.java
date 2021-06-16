package com.willfp.eco.internal;

import com.willfp.eco.core.config.base.LangYml;
import org.jetbrains.annotations.NotNull;

public abstract class InternalInterfacing {
    private static InternalInterfacing interfacing;

    public static void setInterfacing(@NotNull final InternalInterfacing interfacing) {
        InternalInterfacing.interfacing = interfacing;
    }

    public abstract LangYml getLang();

    public static InternalInterfacing getInstance() {
        return interfacing;
    }
}
