package com.willfp.eco.spigot.data.storage

import com.willfp.eco.core.Eco
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.internal.data.EcoPlayerProfile

class ProfileSaver(plugin: EcoPlugin) {
    init {
        plugin.scheduler.runTimer({
            for ((uuid, set) in EcoPlayerProfile.CHANGE_MAP) {
                Eco.getHandler().playerProfileHandler.saveKeysForPlayer(uuid, set)
            }
            EcoPlayerProfile.CHANGE_MAP.clear()
        }, 1, 1)
    }
}