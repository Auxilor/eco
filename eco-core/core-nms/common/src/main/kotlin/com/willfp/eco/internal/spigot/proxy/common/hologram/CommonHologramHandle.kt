package com.willfp.eco.internal.spigot.proxy.common.hologram

import com.willfp.eco.core.integrations.hologram.Billboard
import com.willfp.eco.core.integrations.hologram.HologramOptions
import com.willfp.eco.core.integrations.hologram.TextAlignment
import com.willfp.eco.internal.spigot.proxies.NativeHologramHandle
import com.willfp.eco.internal.spigot.proxy.common.toNMS
import com.willfp.eco.util.StringUtils
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Display
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.PositionMoveRotation
import net.minecraft.world.phys.Vec3
import org.bukkit.Location
import org.bukkit.craftbukkit.CraftWorld
import org.bukkit.entity.Player
import org.joml.Vector3f

/**
 * A packet-based `Display.TextDisplay` entity handle, compiled once against
 * mojang mappings and reused (via reobfuscation) by every version module.
 *
 * The underlying [Display.TextDisplay] is constructed bound to a [net.minecraft.server.level.ServerLevel]
 * so entity-local NMS logic (position, level-scoped lookups, etc.) works normally,
 * but it is never added to the level - it only ever exists as packet payloads sent
 * to individual players.
 */
class CommonHologramHandle private constructor(
    private val display: Display.TextDisplay
) : NativeHologramHandle {

    override val entityId: Int
        get() = display.id

    override fun spawn(player: Player) {
        val nms = player.toNMS()
        nms.connection.send(buildAddEntityPacket())
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

    /**
     * Because the fake entity is never tracked by a [net.minecraft.server.level.ServerEntity],
     * the add-entity packet is built directly from the entity's own state rather than via
     * `ServerEntity`-based helpers.
     */
    private fun buildAddEntityPacket(): ClientboundAddEntityPacket =
        ClientboundAddEntityPacket(
            display.id,
            display.uuid,
            display.getX(),
            display.getY(),
            display.getZ(),
            display.getXRot(),
            display.getYRot(),
            EntityType.TEXT_DISPLAY,
            0,
            Vec3.ZERO,
            0.0
        )

    private fun sendData(nms: ServerPlayer) {
        val values = display.entityData.nonDefaultValues
        if (!values.isNullOrEmpty()) {
            nms.connection.send(ClientboundSetEntityDataPacket(display.id, values))
        }
    }

    companion object {
        fun create(location: Location, options: HologramOptions): CommonHologramHandle {
            val level = (location.world as CraftWorld).handle

            val display = Display.TextDisplay(EntityType.TEXT_DISPLAY, level)
            display.setId(nextHologramEntityId())
            display.setPos(location.x, location.y, location.z)
            display.setYRot(location.yaw)
            display.setXRot(location.pitch)

            applyOptions(display, options)
            applyText(display, options.contents)

            return CommonHologramHandle(display)
        }

        private fun applyText(display: Display.TextDisplay, contents: List<String>) {
            val joined = contents.joinToString("\n")
            val component: Component = StringUtils.toComponent(joined).toNMS()
            display.entityData.set(TEXT_DISPLAY_TEXT, component)
        }

        private fun applyOptions(display: Display.TextDisplay, options: HologramOptions) {
            val billboard = when (options.billboard) {
                Billboard.FIXED -> Display.BillboardConstraints.FIXED
                Billboard.VERTICAL -> Display.BillboardConstraints.VERTICAL
                Billboard.HORIZONTAL -> Display.BillboardConstraints.HORIZONTAL
                Billboard.CENTER -> Display.BillboardConstraints.CENTER
            }
            display.entityData.set(DISPLAY_BILLBOARD_CONSTRAINTS, billboardId(billboard))
            display.entityData.set(DISPLAY_VIEW_RANGE, options.viewRange)
            display.entityData.set(DISPLAY_SCALE, Vector3f(options.scale, options.scale, options.scale))

            options.backgroundColor?.let { display.entityData.set(TEXT_DISPLAY_BACKGROUND_COLOR, it) }
            options.lineWidth?.let { display.entityData.set(TEXT_DISPLAY_LINE_WIDTH, it) }
            options.textOpacity?.let { display.entityData.set(TEXT_DISPLAY_TEXT_OPACITY, it) }

            var flags = 0
            if (options.hasTextShadow()) flags = flags or Display.TextDisplay.FLAG_SHADOW.toInt()
            if (options.isSeeThrough()) flags = flags or Display.TextDisplay.FLAG_SEE_THROUGH.toInt()
            if (options.backgroundColor == null) flags = flags or Display.TextDisplay.FLAG_USE_DEFAULT_BACKGROUND.toInt()
            flags = flags or when (options.alignment) {
                // Neither alignment bit set means centered - there is no FLAG_ALIGN_CENTER constant.
                TextAlignment.CENTER -> 0
                TextAlignment.LEFT -> Display.TextDisplay.FLAG_ALIGN_LEFT.toInt()
                TextAlignment.RIGHT -> Display.TextDisplay.FLAG_ALIGN_RIGHT.toInt()
            }
            display.entityData.set(TEXT_DISPLAY_STYLE_FLAGS, flags.toByte())
        }

        // ---------------------------------------------------------------
        // Reflective accessor lookups.
        //
        // Display / Display.TextDisplay expose their per-field mutators (setText,
        // setLineWidth, setBackgroundColor, setFlags, setBillboardConstraints,
        // setViewRange, setTransformation, ...) as *private* instance methods, and
        // their `EntityDataAccessor` constants as *private static* fields. There is
        // no public setter surface at all in this mapping, so every value is applied
        // by writing straight into the entity's `SynchedEntityData` (which itself is
        // public via `Entity#getEntityData`) using the accessor constants recovered
        // via reflection here, once, at class-init time.
        // ---------------------------------------------------------------

        @Suppress("UNCHECKED_CAST")
        private fun <T> staticAccessor(clazz: Class<*>, name: String): EntityDataAccessor<T> {
            val field = clazz.getDeclaredField(name)
            field.isAccessible = true
            return field.get(null) as EntityDataAccessor<T>
        }

        private fun billboardId(constraints: Display.BillboardConstraints): Byte {
            val method = Display.BillboardConstraints::class.java.getDeclaredMethod("getId")
            method.isAccessible = true
            return method.invoke(constraints) as Byte
        }

        private val DISPLAY_BILLBOARD_CONSTRAINTS: EntityDataAccessor<Byte> =
            staticAccessor(Display::class.java, "DATA_BILLBOARD_RENDER_CONSTRAINTS_ID")

        private val DISPLAY_VIEW_RANGE: EntityDataAccessor<Float> =
            staticAccessor(Display::class.java, "DATA_VIEW_RANGE_ID")

        private val DISPLAY_SCALE: EntityDataAccessor<Vector3f> =
            staticAccessor(Display::class.java, "DATA_SCALE_ID")

        private val TEXT_DISPLAY_TEXT: EntityDataAccessor<Component> =
            staticAccessor(Display.TextDisplay::class.java, "DATA_TEXT_ID")

        private val TEXT_DISPLAY_LINE_WIDTH: EntityDataAccessor<Int> =
            staticAccessor(Display.TextDisplay::class.java, "DATA_LINE_WIDTH_ID")

        private val TEXT_DISPLAY_BACKGROUND_COLOR: EntityDataAccessor<Int> =
            staticAccessor(Display.TextDisplay::class.java, "DATA_BACKGROUND_COLOR_ID")

        private val TEXT_DISPLAY_TEXT_OPACITY: EntityDataAccessor<Byte> =
            staticAccessor(Display.TextDisplay::class.java, "DATA_TEXT_OPACITY_ID")

        private val TEXT_DISPLAY_STYLE_FLAGS: EntityDataAccessor<Byte> =
            staticAccessor(Display.TextDisplay::class.java, "DATA_STYLE_FLAGS_ID")
    }
}
