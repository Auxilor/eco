package com.willfp.eco.internal.integrations

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.integrations.placeholder.PlaceholderIntegration
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import me.clip.placeholderapi.PlaceholderAPI
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player

class PlaceholderIntegrationPAPI(private val plugin: EcoPlugin) : PlaceholderExpansion(), PlaceholderIntegration {
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

    override fun registerIntegration() {
        register()
    }

    override fun getPluginName(): String {
        return "PlaceholderAPI"
    }

    override fun translate(
        text: String,
        player: Player?
    ): String {
        return PlaceholderAPI.setPlaceholders(player, text)
    }

    override fun findPlaceholdersIn(text: String): MutableList<String> {
        val placeholders = mutableListOf<String>()
        val matcher = PlaceholderAPI.getPlaceholderPattern().matcher(text)
        while (matcher.find()) {
            placeholders.add(matcher.group())
        }

        return placeholders
    }
}