package com.willfp.eco.core.config.yaml.wrapper;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.config.interfaces.WrappedYamlConfiguration;
import com.willfp.eco.core.config.wrapper.ConfigWrapper;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Wrapper to handle the backend yaml config implementations.
 *
 * @deprecated JSON and yml have full parity, use configs without a prefix instead,
 * eg {@link com.willfp.eco.core.config.TransientConfig}, {@link com.willfp.eco.core.config.BaseConfig}.
 * These configs will be removed eventually.
 */
@SuppressWarnings("removal")
@Deprecated(since = "6.17.0", forRemoval = true)
@ApiStatus.ScheduledForRemoval(inVersion = "6.30.0")
public abstract class YamlConfigWrapper extends ConfigWrapper<Config> implements WrappedYamlConfiguration {
    /**
     * Create a config wrapper.
     *
     * @param handle The handle.
     */
    protected YamlConfigWrapper(@NotNull final Config handle) {
        super(handle);
    }

    @Override
    public YamlConfiguration getBukkitHandle() {
        return ((WrappedYamlConfiguration) this.getHandle()).getBukkitHandle();
    }

    static {
        Bukkit.getLogger().severe("JSONConfig is scheduled for removal in the next release!");
    }
}
