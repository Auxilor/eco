package com.willfp.eco.internal.spigot.proxies

import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.UUID

interface WaypointHandlerProxy {
    /**
     * Show a waypoint to a single player on their locator bar.
     *
     * @param color Packed RGB, or null to use the client's default style colour.
     */
    fun showWaypoint(viewer: Player, id: UUID, location: Location, color: Int?)

    fun hideWaypoint(viewer: Player, id: UUID)
}
