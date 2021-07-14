package com.willfp.eco.core.config.interfaces;

import org.bukkit.configuration.file.YamlConfiguration;

public interface WrappedYamlConfiguration {
    /**
     * Get the ConfigurationSection handle.
     *
     * @return The handle.
     */
    YamlConfiguration getBukkitHandle();
}
