package com.willfp.eco.core.config.wrapper;

import com.willfp.eco.core.config.interfaces.LoadableConfig;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

/**
 * Wrapper to handle the backend loadable yaml config implementations.
 */
public abstract class LoadableConfigWrapper extends ConfigWrapper<LoadableConfig> implements LoadableConfig {
    /**
     * Create a config wrapper.
     *
     * @param handle The handle.
     */
    protected LoadableConfigWrapper(@NotNull final LoadableConfig handle) {
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

    @Override
    public @NotNull YamlConfiguration toBukkit() {
        return this.getHandle().toBukkit();
    }
}
