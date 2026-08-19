package com.willfp.eco.internal.spigot.proxy.common.packet

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.internal.spigot.proxy.common.toNMS
import java.util.concurrent.atomic.AtomicBoolean
import java.util.logging.Logger
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

private const val BASE_NAME = "packet_handler"
private const val ECO_NAME = "eco_packets"

object PacketInjectorListener : Listener {
    private val logger: Logger
        get() = EcoPlugin.getPlugin("eco")?.logger ?: Bukkit.getLogger()

    /*
    Players connecting through something that doesn't use a standard netty pipeline, most
    commonly Geyser running in plugin mode, may not have the handler eco injects before.
    That isn't fatal, but it silently disables the display module for that player, so it's
    worth saying so rather than leaving it to be discovered as a mystery.
     */
    private val hasWarnedAboutPipeline = AtomicBoolean(false)

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player

        val channel = player.toNMS().connection.connection.channel

        val names = channel.pipeline().names()

        if (BASE_NAME !in names) {
            if (hasWarnedAboutPipeline.compareAndSet(false, true)) {
                logger.warning("Could not find '$BASE_NAME' in the netty pipeline for ${player.name}")
                logger.warning("eco packet modifications (including the display module) are disabled for them.")
                logger.warning("This is expected for players connecting through a non-standard pipeline,")
                logger.warning("such as Geyser running in plugin mode. Pipeline: ${names.joinToString(", ")}")
                logger.warning("Further occurrences will not be logged.")
            } else {
                logger.fine("Skipped packet injection for ${player.name}: no '$BASE_NAME' in pipeline")
            }

            return
        }

        if (ECO_NAME in names) {
            return
        }

        channel.pipeline().addBefore(BASE_NAME, ECO_NAME, EcoChannelDuplexHandler(player.uniqueId))
    }

    @EventHandler
    fun onLeave(event: PlayerQuitEvent) {
        val player = event.player

        val channel = player.toNMS().connection.connection.channel

        channel.eventLoop().submit {
            if (channel.pipeline().get(ECO_NAME) != null) {
                channel.pipeline().remove(ECO_NAME)
            }
        }
    }
}
