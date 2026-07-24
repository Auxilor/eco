package com.willfp.eco.internal.spigot.hologram

import com.willfp.eco.core.EcoPlugin
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.player.PlayerTeleportEvent

class HologramTracker(
    private val plugin: EcoPlugin
) : Listener {
    private val holograms: MutableSet<EcoHologram> = ConcurrentHashMap.newKeySet()

    // Per-player: hologram entity ids currently sent to that player.
    private val sent: MutableMap<UUID, MutableSet<Int>> = ConcurrentHashMap()

    fun register(hologram: EcoHologram) {
        holograms.add(hologram)
        for (player in Bukkit.getOnlinePlayers()) {
            reconcile(player, hologram)
        }
    }

    fun unregister(hologram: EcoHologram) {
        holograms.remove(hologram)
        val id = hologram.handle.entityId
        for (player in Bukkit.getOnlinePlayers()) {
            if (sent[player.uniqueId]?.remove(id) == true) {
                hologram.handle.despawn(player)
            }
        }
    }

    fun pushContents(hologram: EcoHologram) {
        val id = hologram.handle.entityId
        for (player in Bukkit.getOnlinePlayers()) {
            if (sent[player.uniqueId]?.contains(id) == true) {
                hologram.handle.updateData(player, hologram.currentContents())
            }
        }
    }

    fun pushLocation(hologram: EcoHologram) {
        val id = hologram.handle.entityId
        for (player in Bukkit.getOnlinePlayers()) {
            if (sent[player.uniqueId]?.contains(id) == true) {
                hologram.handle.updateLocation(player, hologram.getLocation())
            }
        }
        // Players who now enter/leave range are handled by the next reconcile tick.
    }

    fun refreshFor(player: Player) {
        for (hologram in holograms) {
            reconcile(player, hologram)
        }
    }

    /** Bring a single player's view of a single hologram in line with what it should be. */
    private fun reconcile(player: Player, hologram: EcoHologram) {
        val id = hologram.handle.entityId
        val playerSent = sent.computeIfAbsent(player.uniqueId) { ConcurrentHashMap.newKeySet() }
        val should = shouldShow(player, hologram)
        val currentlySent = playerSent.contains(id)

        if (should && !currentlySent) {
            // spawn() already sends a metadata packet built from the entity's current
            // (already up to date) entityData, so a follow-up updateData() here would
            // just resend the same contents a second time.
            hologram.handle.spawn(player)
            playerSent.add(id)
        } else if (!should && currentlySent) {
            hologram.handle.despawn(player)
            playerSent.remove(id)
        }
    }

    private fun shouldShow(player: Player, hologram: EcoHologram): Boolean {
        if (!hologram.shouldSee(player)) return false
        val loc = hologram.getLocation()
        val world = loc.world ?: return false
        if (player.world != world) return false
        val range = player.clientViewDistance.coerceAtMost(Bukkit.getViewDistance()) * 16.0
        return player.location.distanceSquared(loc) <= range * range
    }

    fun start() {
        plugin.eventManager.registerListener(this)
        // Reconcile every second; cheap relative to network, robust to teleports/mounts.
        plugin.scheduler.runTimer(20L, 20L) {
            for (player in Bukkit.getOnlinePlayers()) {
                refreshFor(player)
            }
        }
    }

    fun shutdown() {
        for (hologram in holograms) {
            val id = hologram.handle.entityId
            for (player in Bukkit.getOnlinePlayers()) {
                if (sent[player.uniqueId]?.remove(id) == true) {
                    hologram.handle.despawn(player)
                }
            }
        }
        holograms.clear()
        sent.clear()
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        refreshFor(event.player)
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        sent.remove(event.player.uniqueId)
    }

    @EventHandler
    fun onWorld(event: PlayerChangedWorldEvent) {
        refreshFor(event.player)
    }

    @EventHandler
    fun onRespawn(event: PlayerRespawnEvent) {
        // Client discards entities on respawn; drop our record so the next tick re-sends.
        sent.remove(event.player.uniqueId)
    }

    @EventHandler
    fun onTeleport(event: PlayerTeleportEvent) {
        refreshFor(event.player)
    }
}
