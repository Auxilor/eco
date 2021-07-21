package com.willfp.eco.core.config.json.wrapper;

import com.willfp.eco.core.config.interfaces.JSONConfig;
import com.willfp.eco.core.config.wrapper.ConfigWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Wrapper to handle the backend JSON config implementations.
 */
public abstract class JSONConfigWrapper extends ConfigWrapper<JSONConfig> implements JSONConfig {
    /**
     * Create a config wrapper.
     *
     * @param handle The handle.
     */
    protected JSONConfigWrapper(@NotNull final JSONConfig handle) {
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

    @Override
    public JSONConfig clone() {
        return this.getHandle().clone();
    }
}
