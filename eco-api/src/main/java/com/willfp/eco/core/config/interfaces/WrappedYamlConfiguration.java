package com.willfp.eco.core.config.interfaces;

import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Interface for configs that wrap an {@link YamlConfiguration}.
 *
 * @see com.willfp.eco.core.config.yaml.wrapper.YamlConfigWrapper
 */
@Deprecated
@SuppressWarnings("DeprecatedIsStillUsed")
public interface WrappedYamlConfiguration {
    /**
     * Get the ConfigurationSection handle.
     *
     * @return The handle.
     */
    YamlConfiguration getBukkitHandle();
}
