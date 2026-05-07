package com.willfp.eco.internal.config.handler

import com.willfp.eco.core.config.interfaces.LoadableConfig
import com.willfp.eco.core.config.updating.ConfigHandler
import com.willfp.eco.internal.config.EcoLoadableConfig
import com.willfp.eco.internal.config.EcoUpdatableConfig

open class SimpleConfigHandler : ConfigHandler {
    private val configs = mutableListOf<LoadableConfig>()

    override fun saveAllConfigs() {
        for (config in configs) {
            config.save()
        }
    }

    override fun addConfig(config: LoadableConfig) {
        configs.add(config)
    }

    override fun updateConfigs() {
        for (config in configs) {
            when (config) {
                is EcoUpdatableConfig -> config.update()
                is EcoLoadableConfig -> config.reloadFromFile()
            }
        }
    }
}
