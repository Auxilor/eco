package com.willfp.eco.internal.config;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class ConfigSection extends ConfigWrapper<ConfigurationSection> {
    /**
     * Config section.
     *
     * @param section    The section.
     */
    protected ConfigSection(@NotNull final ConfigurationSection section) {
        this.init(section);
    }
}
