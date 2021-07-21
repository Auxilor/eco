package com.willfp.eco.internal.config.yaml;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class EcoYamlConfigSection extends EcoYamlConfigWrapper<ConfigurationSection> {
    public EcoYamlConfigSection(@NotNull final ConfigurationSection section) {
        this.init(section);
    }
}
