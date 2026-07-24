package com.willfp.eco.internal.spigot.proxy.v1_21_10.hologram

import com.willfp.eco.core.integrations.hologram.Billboard
import com.willfp.eco.core.integrations.hologram.HologramOptions
import com.willfp.eco.core.integrations.hologram.TextAlignment
import com.willfp.eco.internal.spigot.proxies.NativeHologramHandle
import com.willfp.eco.internal.spigot.proxy.common.toNMS
import com.willfp.eco.util.StringUtils
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket
import net.minecraft.server.level.ServerEntity
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Display
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.PositionMoveRotation
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.craftbukkit.CraftWorld
import org.bukkit.entity.Player
import org.bukkit.util.Transformation
import org.joml.Quaternionf
import org.joml.Vector3f
import java.util.function.Predicate

/**
 * Packet-based [Display.TextDisplay] hologram for 1.21.10+, where Paper collapsed
 * ServerEntity's separate broadcast Consumer/BiConsumer params into a single
 * [ServerEntity.Synchronizer]. This is a straight copy of the pre-1.21.10
 * `CommonHologramHandle` with only that constructor call updated - the shared
 * `common` module is compiled once against the 1.21.8 mappings and reused
 * as-is by every other version, so it can't track this kind of NMS constructor
 * change; versions where the shape changes need their own copy here instead.
 */
class V1_21_10HologramHandle private constructor(
    private val display: Display.TextDisplay
) : NativeHologramHandle {

    override val entityId: Int
        get() = display.id

    override fun spawn(player: Player) {
        val nms = player.toNMS()

        // ClientboundAddEntityPacket(Entity, ServerEntity) reads the entity's real double
        // position; the (Entity, int, BlockPos) overload would floor it to block coordinates.
        // The ServerEntity is a throwaway built purely to produce the positioned packet.
        val synchronizer = object : ServerEntity.Synchronizer {
            override fun sendToTrackingPlayers(packet: Packet<in ClientGamePacketListener>) {}
            override fun sendToTrackingPlayersAndSelf(packet: Packet<in ClientGamePacketListener>) {}
            override fun sendToTrackingPlayersFiltered(
                packet: Packet<in ClientGamePacketListener>,
                predicate: Predicate<ServerPlayer>
            ) {}
        }
        val serverEntity = ServerEntity(
            display.level() as ServerLevel,
            display,
            0,
            false,
            synchronizer,
            emptySet() // Paper's trackedPlayers param; unused for a throwaway, one-shot packet builder
        )
        nms.connection.send(ClientboundAddEntityPacket(display, serverEntity))
        sendData(nms)
    }

    override fun despawn(player: Player) {
        player.toNMS().connection.send(ClientboundRemoveEntitiesPacket(display.id))
    }

    override fun updateData(player: Player, contents: List<String>) {
        applyText(display, contents)
        sendData(player.toNMS())
    }

    override fun updateLocation(player: Player, location: Location) {
        display.setPos(location.x, location.y, location.z)
        display.setYRot(location.yaw)
        display.setXRot(location.pitch)

        player.toNMS().connection.send(
            ClientboundTeleportEntityPacket.teleport(
                display.id,
                PositionMoveRotation.of(display),
                emptySet(),
                display.onGround()
            )
        )
    }

    private fun sendData(nms: ServerPlayer) {
        val values = display.entityData.nonDefaultValues
        if (!values.isNullOrEmpty()) {
            nms.connection.send(ClientboundSetEntityDataPacket(display.id, values))
        }
    }

    companion object {
        fun create(location: Location, options: HologramOptions): V1_21_10HologramHandle {
            val world = location.world
                ?: throw IllegalArgumentException("Hologram location must have a non-null world")
            val level = (world as CraftWorld).handle

            // The TextDisplay constructor assigns a unique entity id from the vanilla counter.
            val display = Display.TextDisplay(EntityType.TEXT_DISPLAY, level)
            display.setPos(location.x, location.y, location.z)
            display.setYRot(location.yaw)
            display.setXRot(location.pitch)

            applyOptions(display, options)
            applyText(display, options.contents)

            return V1_21_10HologramHandle(display)
        }

        private fun bukkitOf(display: Display.TextDisplay): org.bukkit.entity.TextDisplay =
            display.bukkitEntity as org.bukkit.entity.TextDisplay

        private fun applyText(display: Display.TextDisplay, contents: List<String>) {
            val joined = contents.joinToString("\n")
            bukkitOf(display).text(StringUtils.toComponent(joined))
        }

        private fun applyOptions(display: Display.TextDisplay, options: HologramOptions) {
            val bukkit = bukkitOf(display)

            bukkit.billboard = when (options.billboard) {
                Billboard.FIXED -> org.bukkit.entity.Display.Billboard.FIXED
                Billboard.VERTICAL -> org.bukkit.entity.Display.Billboard.VERTICAL
                Billboard.HORIZONTAL -> org.bukkit.entity.Display.Billboard.HORIZONTAL
                Billboard.CENTER -> org.bukkit.entity.Display.Billboard.CENTER
            }

            bukkit.viewRange = options.viewRange

            val scale = options.scale
            bukkit.transformation = Transformation(
                Vector3f(0f, 0f, 0f),
                Quaternionf(),
                Vector3f(scale, scale, scale),
                Quaternionf()
            )

            val backgroundColor = options.backgroundColor
            if (backgroundColor != null) {
                bukkit.isDefaultBackground = false
                bukkit.backgroundColor = Color.fromARGB(backgroundColor)
            } else {
                bukkit.isDefaultBackground = true
            }

            options.lineWidth?.let { bukkit.lineWidth = it }
            options.textOpacity?.let { bukkit.textOpacity = it }

            bukkit.isShadowed = options.hasTextShadow()
            bukkit.isSeeThrough = options.isSeeThrough()

            bukkit.alignment = when (options.alignment) {
                TextAlignment.CENTER -> org.bukkit.entity.TextDisplay.TextAlignment.CENTER
                TextAlignment.LEFT -> org.bukkit.entity.TextDisplay.TextAlignment.LEFT
                TextAlignment.RIGHT -> org.bukkit.entity.TextDisplay.TextAlignment.RIGHT
            }
        }
    }
}
