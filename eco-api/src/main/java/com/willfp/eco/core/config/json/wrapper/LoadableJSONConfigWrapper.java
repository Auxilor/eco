package com.willfp.eco.core.config.json.wrapper;

import com.willfp.eco.core.config.LoadableConfig;
import com.willfp.eco.core.config.json.LoadableJSONConfig;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public abstract class LoadableJSONConfigWrapper extends JSONConfigWrapper<LoadableJSONConfig> implements LoadableConfig {
    /**
     * Create a config wrapper.
     *
     * @param handle The handle.
     */
    public LoadableJSONConfigWrapper(@NotNull final LoadableJSONConfig handle) {
        super(handle);
    }


    @Override
    public void createFile() {
        this.getHandle().createFile();
    }

    @Override
    public String getResourcePath() {
        return this.getHandle().getResourcePath();
    }

    @Override
    public void save() throws IOException {
        this.getHandle().save();
    }

    @Override
    public File getConfigFile() {
        return this.getHandle().getConfigFile();
    }

    @Override
    public String getName() {
        return this.getHandle().getName();
    }
}
