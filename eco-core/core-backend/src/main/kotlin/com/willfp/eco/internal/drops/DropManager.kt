package com.willfp.eco.internal.drops

import com.willfp.eco.core.EcoPlugin

object DropManager {
    var type = DropQueueType.COLLATED

    fun update(plugin: EcoPlugin) {
        type = if (plugin.configYml.getBool("use-fast-collated-drops")) DropQueueType.COLLATED else DropQueueType.STANDARD
    }
}