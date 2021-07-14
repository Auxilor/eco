package com.willfp.eco.core.config.yaml.wrapper;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.config.interfaces.LoadableConfig;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public abstract class LoadableYamlConfigWrapper extends YamlConfigWrapper implements LoadableConfig {
    /**
     * Create a config wrapper.
     *
     * @param handle The handle.
     */
    protected LoadableYamlConfigWrapper(@NotNull final Config handle) {
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
