package com.willfp.eco.internal.config.yaml;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class EcoYamlConfigSection extends EcoYamlConfigWrapper<ConfigurationSection> {
    /**
     * Config section.
     *
     * @param section The section.
     */
    public EcoYamlConfigSection(@NotNull final ConfigurationSection section) {
        this.init(section);
    }
}
