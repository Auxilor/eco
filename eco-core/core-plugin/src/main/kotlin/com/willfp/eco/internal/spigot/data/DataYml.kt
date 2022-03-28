package com.willfp.eco.internal.spigot.data

import com.willfp.eco.core.config.BaseConfig
import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.internal.spigot.EcoSpigotPlugin

class DataYml(
    plugin: EcoSpigotPlugin
) : BaseConfig(
    "data",
    plugin,
    false,
    ConfigType.YAML,
    false
)
