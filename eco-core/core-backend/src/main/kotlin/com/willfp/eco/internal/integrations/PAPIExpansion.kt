package com.willfp.eco.internal.integrations

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.eco.core.placeholder.context.placeholderContext
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player

@Suppress("DEPRECATION")
class PAPIExpansion(private val plugin: EcoPlugin) : PlaceholderExpansion() {
    init {
        register()
    }

    override fun persist(): Boolean {
        return true
    }

    override fun canRegister(): Boolean {
        return true
    }

    override fun getAuthor(): String {
        return java.lang.String.join(", ", plugin.description.authors)
    }

    override fun getIdentifier(): String {
        return plugin.description.name.lowercase()
    }

    override fun getVersion(): String {
        return plugin.description.version
    }

    override fun onPlaceholderRequest(
        player: Player?,
        identifier: String
    ): String? {
        return PlaceholderManager.getResult(
            plugin,
            identifier,
            placeholderContext(
                player = player,
                item = player?.inventory?.itemInMainHand
            )
        )
    }

    override fun getPlaceholders(): List<String> {
        return PlaceholderManager.getRegisteredPlaceholders(plugin)
            .map { "%${this.plugin.name.lowercase()}_${it.pattern.pattern()}%" }
    }
}
