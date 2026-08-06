package com.willfp.eco.core.config.wrapper;

import com.willfp.eco.core.config.interfaces.LoadableConfig;
import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

/**
 * Wrapper to handle the backend {@link LoadableConfig} implementations.
 */
public abstract class LoadableConfigWrapper extends ConfigWrapper<LoadableConfig> implements LoadableConfig {
    /**
     * Create a loadable config wrapper.
     *
     * @param handle The config that is being wrapped.
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
