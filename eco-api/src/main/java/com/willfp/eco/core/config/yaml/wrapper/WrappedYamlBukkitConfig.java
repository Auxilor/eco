package com.willfp.eco.core.config.yaml.wrapper;

import org.bukkit.configuration.ConfigurationSection;

public interface WrappedYamlBukkitConfig<T extends ConfigurationSection> {
    /**
     * Get the ConfigurationSection handle.
     *
     * @return The handle.
     */
    T getBukkitHandle();
}
