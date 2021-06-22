package com.willfp.eco.internal.config.yaml;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class YamlConfigSection extends YamlConfigWrapper<ConfigurationSection> {
    /**
     * Config section.
     *
     * @param section The section.
     */
    public YamlConfigSection(@NotNull final ConfigurationSection section) {
        this.init(section);
    }
}
