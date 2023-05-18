package com.willfp.eco.internal.spigot.data.storage

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.internal.spigot.data.EcoProfile
import com.willfp.eco.internal.spigot.data.ProfileHandler

class ProfileSaver(
    private val plugin: EcoPlugin,
    private val handler: ProfileHandler
) {
    fun startTicking() {
        val interval = plugin.configYml.getInt("save-interval").toLong()

        plugin.scheduler.runTimer(20, interval) {
            val iterator = EcoProfile.CHANGE_MAP.iterator()

            while (iterator.hasNext()) {
                val uuid = iterator.next()
                iterator.remove()

                val profile = handler.accessLoadedProfile(uuid) ?: continue

                handler.saveKeysFor(uuid, profile.data.keys)
            }
        }
    }
}
