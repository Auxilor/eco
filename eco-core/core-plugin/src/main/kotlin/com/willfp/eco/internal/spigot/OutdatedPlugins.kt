package com.willfp.eco.internal.spigot

import com.willfp.eco.core.Eco
import com.willfp.eco.core.version.Version
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerLoadEvent

/**
 * Tracks plugins that failed to load because they require a newer version of eco than
 * the one that is running, and stops the server before anyone can join.
 *
 * A plugin that fails this check is still installed, so leaving the server running means
 * playing a whole session with that plugin's items and data unregistered, which lets other
 * plugins strip or overwrite them. Shutting down keeps that data intact.
 */
object OutdatedPlugins : Listener {
    private val outdated = mutableMapOf<String, Version>()

    fun register(pluginName: String, requiredVersion: Version) {
        outdated[pluginName] = requiredVersion

        // The server is locked immediately, as the shutdown only happens once the server
        // has finished loading, and players can log in before then.
        ServerLocking.lock("Outdated version of eco! Check console for more information.")
    }

    /**
     * Shut down once the server has finished loading; worlds and player data are only
     * saved properly from this point onwards.
     */
    @EventHandler
    fun handle(event: ServerLoadEvent) {
        if (outdated.isEmpty()) {
            return
        }

        val plugin = Eco.get().ecoPlugin

        if (!(plugin.configYml.getBoolOrNull("shutdown-on-outdated-plugin") ?: true)) {
            ServerLocking.unlock()
            return
        }

        plugin.logger.severe("Shutting down the server: you are running an outdated version of eco!")

        for ((name, version) in outdated) {
            plugin.logger.severe("- $name requires at least eco $version")
        }

        plugin.logger.severe("These plugins failed to load, so their items and data are")
        plugin.logger.severe("unregistered, which can cause other plugins to delete them.")
        plugin.logger.severe("Update eco here: https://polymart.org/product/773/eco")
        plugin.logger.severe("To start anyway, set shutdown-on-outdated-plugin to false in eco's config.yml.")

        Bukkit.shutdown()
    }
}
