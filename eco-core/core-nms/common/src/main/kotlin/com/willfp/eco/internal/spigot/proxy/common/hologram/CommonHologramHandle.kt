package com.willfp.eco.internal.spigot.proxy.common.hologram

import com.willfp.eco.core.integrations.hologram.Billboard
import com.willfp.eco.core.integrations.hologram.HologramOptions
import com.willfp.eco.core.integrations.hologram.TextAlignment
import com.willfp.eco.internal.spigot.proxies.NativeHologramHandle
import com.willfp.eco.internal.spigot.proxy.common.toNMS
import com.willfp.eco.util.StringUtils
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket
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

/**
 * A packet-based `Display.TextDisplay` entity handle, compiled once against
 * mojang mappings and reused (via reobfuscation) by every version module.
 *
 * The underlying [Display.TextDisplay] is constructed bound to a [net.minecraft.server.level.ServerLevel]
 * so entity-local NMS logic (position, level-scoped lookups, etc.) works normally,
 * but it is never added to the level - it only ever exists as packet payloads sent
 * to individual players.
 *
 * IMPORTANT: eco reobfuscates this module's compiled classes to Spigot (obfuscated)
 * mappings per Minecraft version at build time. Direct Kotlin references to
 * `net.minecraft.*` members (fields/methods/constructors written literally in source)
 * get rewritten by that reobfuscation pass and stay correct at runtime. Reflection
 * that looks members up *by mojang-mapped name* does NOT get rewritten - at runtime it
 * would search for a field/method name that no longer exists (obfuscated to something
 * like a single letter), throwing NoSuchFieldException/NoSuchMethodException. So all
 * mutable render state below is applied through the stable, never-obfuscated
 * Bukkit/Paper API (`entity.getBukkitEntity()` as `org.bukkit.entity.TextDisplay`)
 * instead of via reflection into NMS internals.
 */
class CommonHologramHandle private constructor(
    private val display: Display.TextDisplay
) : NativeHologramHandle {

    override val entityId: Int
        get() = display.id

    override fun spawn(player: Player) {
        val nms = player.toNMS()
        nms.connection.send(ClientboundAddEntityPacket(display, 0, display.blockPosition()))
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
        fun create(location: Location, options: HologramOptions): CommonHologramHandle {
            val level = (location.world as CraftWorld).handle

            // The Display.TextDisplay constructor already assigns the entity a fresh,
            // globally unique id from the vanilla entity counter - no separate id
            // allocation is necessary.
            val display = Display.TextDisplay(EntityType.TEXT_DISPLAY, level)
            display.setPos(location.x, location.y, location.z)
            display.setYRot(location.yaw)
            display.setXRot(location.pitch)

            applyOptions(display, options)
            applyText(display, options.contents)

            return CommonHologramHandle(display)
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
