package com.willfp.eco.internal.spigot.proxy.v1_21_5

import com.willfp.eco.core.packet.Packet
import com.willfp.eco.core.packet.sendPacket
import com.willfp.eco.internal.spigot.proxy.DisplayNameProxy
import com.willfp.eco.internal.spigot.proxy.common.toNMS
import net.kyori.adventure.text.Component
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.Entity
import org.bukkit.craftbukkit.entity.CraftLivingEntity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import java.util.Optional

@Suppress("UNCHECKED_CAST")
class DisplayName : DisplayNameProxy {
    private val displayNameAccessor = Entity::class.java
        .declaredFields
        .filter { it.type == EntityDataAccessor::class.java }
        .toList()[2]
        .apply { isAccessible = true }
        .get(null) as EntityDataAccessor<Optional<net.minecraft.network.chat.Component>>

    private val customNameVisibleAccessor = Entity::class.java
        .declaredFields
        .filter { it.type == EntityDataAccessor::class.java }
        .toList()[3]
        .apply { isAccessible = true }
        .get(null) as EntityDataAccessor<Boolean>

    override fun setClientsideDisplayName(
        entity: LivingEntity,
        player: Player,
        displayName: Component,
        visible: Boolean
    ) {
        if (entity !is CraftLivingEntity) {
            return
        }

        val nmsComponent = displayName.toNMS()

        val nmsEntity = entity.handle

        val packet = ClientboundSetEntityDataPacket(
            nmsEntity.id,
            listOf(
                SynchedEntityData.DataValue.create(displayNameAccessor, Optional.of(nmsComponent)),
                SynchedEntityData.DataValue.create(customNameVisibleAccessor, visible)
            )
        )

        player.sendPacket(Packet(packet))
    }
}
