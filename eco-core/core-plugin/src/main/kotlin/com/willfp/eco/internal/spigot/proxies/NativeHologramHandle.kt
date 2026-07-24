package com.willfp.eco.internal.spigot.proxies

import org.bukkit.Location
import org.bukkit.entity.Player

/**
 * A version-agnostic handle to a single packet-based hologram entity.
 * All methods target one player; the backend decides which players receive them.
 */
interface NativeHologramHandle {
    /** The fake entity id used in packets. */
    val entityId: Int

    /** Send the spawn + initial metadata packets to [player]. */
    fun spawn(player: Player)

    /** Send the remove-entity packet to [player]. */
    fun despawn(player: Player)

    /** Reformat [contents] and send updated metadata to [player]. */
    fun updateData(player: Player, contents: List<String>)

    /** Send a teleport packet moving the hologram to [location] for [player]. */
    fun updateLocation(player: Player, location: Location)
}
