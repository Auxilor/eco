package com.willfp.eco.internal.config.yaml

import com.willfp.eco.core.placeholder.StaticPlaceholder
import org.bukkit.configuration.ConfigurationSection

class EcoYamlConfigSection(section: ConfigurationSection, injections: Collection<StaticPlaceholder> = emptyList()) : EcoYamlConfigWrapper<ConfigurationSection>() {
    init {
        init(section)
        this.injections = injections.toMutableList()
    }
}