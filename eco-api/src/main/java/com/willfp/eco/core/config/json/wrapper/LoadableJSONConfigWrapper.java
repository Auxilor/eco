package com.willfp.eco.core.config.json.wrapper;

import com.willfp.eco.core.config.interfaces.JSONConfig;
import com.willfp.eco.core.config.interfaces.LoadableConfig;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

/**
 * Wrapper to handle the backend loadable JSON config implementations.
 */
public abstract class LoadableJSONConfigWrapper extends JSONConfigWrapper implements LoadableConfig {
    /**
     * Create a config wrapper.
     *
     * @param handle The handle.
     */
    protected LoadableJSONConfigWrapper(@NotNull final JSONConfig handle) {
        super(handle);

        Validate.isTrue(handle instanceof LoadableConfig, "Wrapped config must be loadable!");
    }


    @Override
    public void createFile() {
        ((LoadableConfig) this.getHandle()).createFile();
    }

    @Override
    public String getResourcePath() {
        return ((LoadableConfig) this.getHandle()).getResourcePath();
    }

    @Override
    public void save() throws IOException {
        ((LoadableConfig) this.getHandle()).save();
    }

    @Override
    public File getConfigFile() {
        return ((LoadableConfig) this.getHandle()).getConfigFile();
    }

    @Override
    public String getName() {
        return ((LoadableConfig) this.getHandle()).getName();
    }
}
