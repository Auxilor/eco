package com.willfp.eco.internal.integrations

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player

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
    ): String {
        return PlaceholderManager.getResult(player, identifier, plugin)
    }
}
