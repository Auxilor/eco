package com.willfp.eco.core.config.wrapper;

import com.willfp.eco.core.config.JSONConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class JSONConfigWrapper extends ConfigWrapper<JSONConfig> implements JSONConfig {
    /**
     * Create a config wrapper.
     *
     * @param handle The handle.
     */
    public JSONConfigWrapper(@NotNull final JSONConfig handle) {
        super(handle);
    }


    @Override
    public @NotNull List<JSONConfig> getSubsections(@NotNull final String path) {
        return this.getHandle().getSubsections(path);
    }

    @Override
    public @Nullable List<JSONConfig> getSubsectionsOrNull(@NotNull final String path) {
        return this.getHandle().getSubsectionsOrNull(path);
    }
}
