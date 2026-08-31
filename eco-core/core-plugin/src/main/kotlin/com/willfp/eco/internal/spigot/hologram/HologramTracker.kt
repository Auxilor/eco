package com.willfp.eco.internal.spigot.hologram

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.scheduling.EcoTask
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

    // Per-player: the recurring reconcile task running on that player's entity scheduler.
    private val refreshTasks = ConcurrentHashMap<UUID, EcoTask>()

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

        for (player in Bukkit.getOnlinePlayers()) {
            startRefreshing(player)
        }
    }

    private fun startRefreshing(player: Player) {
        val existing = refreshTasks.remove(player.uniqueId)
        existing?.cancel()

        // Reconcile every second; cheap relative to network, robust to teleports/mounts.
        refreshTasks[player.uniqueId] = plugin.scheduler.on(player)
            .onRetired { refreshTasks.remove(player.uniqueId) }
            .runTimer(Runnable { refreshFor(player) }, 20L, 20L)
    }

    fun shutdown() {
        for (task in refreshTasks.values) {
            task.cancel()
        }
        refreshTasks.clear()

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
        startRefreshing(event.player)
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        sent.remove(event.player.uniqueId)
        refreshTasks.remove(event.player.uniqueId)?.cancel()
    }

    @EventHandler
    fun onWorld(event: PlayerChangedWorldEvent) {
        refreshFor(event.player)
    }

    @EventHandler
    fun onRespawn(event: PlayerRespawnEvent) {
        // Don't just drop the record and assume the client discarded the entities: if a
        // hologram is unregister()'d in the window between this wipe and the next reconcile
        // tick, unregister() checks `sent` to decide whether to send a despawn packet, finds
        // nothing there, and skips it - stranding the entity on the client until relog. Send
        // the despawn ourselves so removal is never contingent on that race.
        val player = event.player
        val ids = sent.remove(player.uniqueId) ?: return
        for (hologram in holograms) {
            if (hologram.handle.entityId in ids) {
                hologram.handle.despawn(player)
            }
        }
    }

    @EventHandler
    fun onTeleport(event: PlayerTeleportEvent) {
        refreshFor(event.player)
    }
}
