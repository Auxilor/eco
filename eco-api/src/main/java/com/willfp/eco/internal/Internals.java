package com.willfp.eco.internal;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.proxy.AbstractProxy;
import org.jetbrains.annotations.NotNull;

public abstract class Internals {
    private static Internals internals;

    public static void setInterfacing(@NotNull final Internals internals) {
        Internals.internals = internals;
    }

    public abstract EcoPlugin getPlugin();

    public @NotNull abstract <T extends AbstractProxy> T getProxy(@NotNull EcoPlugin plugin,
                                                                   @NotNull Class<T> proxyClass);

    public static Internals getInstance() {
        return internals;
    }
}
