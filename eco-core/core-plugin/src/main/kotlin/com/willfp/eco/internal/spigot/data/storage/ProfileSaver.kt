package com.willfp.eco.internal.spigot.data.storage

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.internal.spigot.data.EcoProfile
import com.willfp.eco.internal.spigot.data.EcoProfileHandler

class ProfileSaver(
    plugin: EcoPlugin,
    handler: EcoProfileHandler
) {
    init {
        plugin.scheduler.runTimer(1, 1) {
            for ((uuid, set) in EcoProfile.CHANGE_MAP) {
                handler.saveKeysFor(uuid, set)
            }
            EcoProfile.CHANGE_MAP.clear()
        }
    }
}
