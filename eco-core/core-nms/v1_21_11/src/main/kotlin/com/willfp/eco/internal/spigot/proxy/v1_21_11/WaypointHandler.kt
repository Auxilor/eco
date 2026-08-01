package com.willfp.eco.internal.spigot.proxy.v1_21_11

import com.willfp.eco.internal.spigot.proxies.WaypointHandlerProxy
import net.minecraft.core.Vec3i
import net.minecraft.network.protocol.game.ClientboundTrackedWaypointPacket
import net.minecraft.world.waypoints.Waypoint
import org.bukkit.Location
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player
import java.util.Optional
import java.util.UUID

class WaypointHandler : WaypointHandlerProxy {
    override fun showWaypoint(viewer: Player, id: UUID, location: Location, color: Int?) {
        if (viewer !is CraftPlayer) {
            return
        }

        val icon = Waypoint.Icon()

        if (color != null) {
            icon.color = Optional.of(color)
        }

        viewer.handle.connection.send(
            ClientboundTrackedWaypointPacket.addWaypointPosition(
                id,
                icon,
                Vec3i(location.blockX, location.blockY, location.blockZ)
            )
        )
    }

    override fun hideWaypoint(viewer: Player, id: UUID) {
        if (viewer !is CraftPlayer) {
            return
        }

        viewer.handle.connection.send(ClientboundTrackedWaypointPacket.removeWaypoint(id))
    }
}
