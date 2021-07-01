package com.willfp.eco.internal;

import com.willfp.eco.core.EcoPlugin;
import org.jetbrains.annotations.NotNull;

public abstract class Internals {
    private static Internals internals;

    public static void setInterfacing(@NotNull final Internals internals) {
        Internals.internals = internals;
    }

    public abstract EcoPlugin getPlugin();

    public static Internals getInstance() {
        return internals;
    }
}
