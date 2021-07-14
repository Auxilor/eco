package com.willfp.eco.core.config.yaml.wrapper;

import com.willfp.eco.core.config.LoadableConfig;
import com.willfp.eco.core.config.yaml.LoadableYamlConfig;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public abstract class LoadableYamlConfigWrapper extends YamlConfigWrapper<LoadableYamlConfig> implements LoadableConfig {
    /**
     * Create a config wrapper.
     *
     * @param handle The handle.
     */
    public LoadableYamlConfigWrapper(@NotNull final LoadableYamlConfig handle) {
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
