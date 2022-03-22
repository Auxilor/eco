package com.willfp.eco.core.config.interfaces;

import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.ApiStatus;

/**
 * Interface for configs that wrap an {@link YamlConfiguration}.
 *
 * @deprecated JSON and yml have full parity, use configs without a prefix instead,
 * eg {@link com.willfp.eco.core.config.TransientConfig}, {@link com.willfp.eco.core.config.BaseConfig}.
 * These configs will be removed eventually.
 */
@Deprecated(since = "6.17.0", forRemoval = true)
@ApiStatus.ScheduledForRemoval(inVersion = "6.30.0")
public interface WrappedYamlConfiguration {
    /**
     * Get the ConfigurationSection handle.
     *
     * @return The handle.
     */
    YamlConfiguration getBukkitHandle();
}
