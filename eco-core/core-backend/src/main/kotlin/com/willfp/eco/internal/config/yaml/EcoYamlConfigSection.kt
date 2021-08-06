package com.willfp.eco.internal.config.yaml

import org.bukkit.configuration.ConfigurationSection

class EcoYamlConfigSection(section: ConfigurationSection) : EcoYamlConfigWrapper<ConfigurationSection>() {
    init {
        init(section)
    }
}