package com.willfp.eco.util

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.Config
import org.bukkit.SoundCategory
import org.bukkit.entity.Player

object SoundConfigUtils {
    fun playIfEnabled(plugin: EcoPlugin, player: Player, configPath: String) {
        val sound = SoundUtils.getSound(plugin.configYml.getFormattedString("$configPath.sound")) ?: return
        val pitch = plugin.configYml.getDoubleFromExpression("$configPath.pitch")
        val volume = plugin.configYml.getDoubleFromExpression("$configPath.volume")
        var categoryString = plugin.configYml.getFormattedStringOrNull("$configPath.category")
        if (categoryString == null) categoryString = "MASTER"
        val category = SoundCategory.valueOf(categoryString.uppercase())

        if (config.getBool("$configPath.enabled")) {
            player.playSound(
                player.location,
                sound,
                category,
                volume.toFloat(),
                pitch.toFloat()
            )
        }
    }
}