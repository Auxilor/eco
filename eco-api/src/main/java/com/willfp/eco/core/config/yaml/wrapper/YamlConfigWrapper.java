package com.willfp.eco.core.config.yaml.wrapper;

import com.willfp.eco.core.config.Config;
import com.willfp.eco.core.config.wrapper.ConfigWrapper;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

public abstract class YamlConfigWrapper<T extends Config> extends ConfigWrapper<T> implements WrappedYamlBukkitConfig<YamlConfiguration> {
    /**
     * Create a config wrapper.
     *
     * @param handle The handle.
     */
    public YamlConfigWrapper(@NotNull final T handle) {
        super(handle);
    }


    @Override
    public YamlConfiguration getBukkitHandle() {
        return (YamlConfiguration) ((WrappedYamlBukkitConfig<?>) this.getHandle()).getBukkitHandle();
    }
}
