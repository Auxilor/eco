package com.willfp.eco.internal.drops

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.updating.ConfigUpdater

object DropManager {
    var type = DropQueueType.COLLATED

    @ConfigUpdater
    @JvmStatic
    fun update(plugin: EcoPlugin) {
        type = if (plugin.configYml.getBool("use-fast-collated-drops")) DropQueueType.COLLATED else DropQueueType.STANDARD
    }
}