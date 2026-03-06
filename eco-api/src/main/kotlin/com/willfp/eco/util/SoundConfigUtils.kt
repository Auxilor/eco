package com.willfp.eco.util

import com.willfp.eco.core.config.interfaces.Config
import org.bukkit.SoundCategory
import org.bukkit.entity.Player

object SoundConfigUtils {
    fun playIfEnabled(config: Config, player: Player, configPath: String) {
        val sound = SoundUtils.getSound(config.getFormattedString("$configPath.sound")) ?: return
        val pitch = config.getDoubleFromExpression("$configPath.pitch")
        val volume = config.getDoubleFromExpression("$configPath.volume")
        var categoryString = config.getFormattedStringOrNull("$configPath.category")
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