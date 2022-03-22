package com.willfp.eco.core.config.yaml.wrapper;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.config.interfaces.LoadableConfig;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

/**
 * Wrapper to handle the backend loadable yaml config implementations.
 *
 * @deprecated JSON and yml have full parity, use configs without a prefix instead,
 * eg {@link com.willfp.eco.core.config.TransientConfig}, {@link com.willfp.eco.core.config.BaseConfig}.
 * These configs will be removed eventually.
 */
@SuppressWarnings("removal")
@Deprecated(since = "6.17.0", forRemoval = true)
@ApiStatus.ScheduledForRemoval(inVersion = "6.30.0")
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

    @Override
    public @Nullable YamlConfiguration getBukkitHandle() {
        return ((LoadableConfig) this.getHandle()).getBukkitHandle();
    }
}
