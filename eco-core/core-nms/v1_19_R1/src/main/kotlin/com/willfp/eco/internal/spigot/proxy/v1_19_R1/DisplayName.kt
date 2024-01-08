package com.willfp.eco.internal.spigot.proxy.v1_19_R1

import com.willfp.eco.core.packet.Packet
import com.willfp.eco.core.packet.sendPacket
import com.willfp.eco.internal.spigot.proxy.DisplayNameProxy
import com.willfp.eco.internal.spigot.proxy.common.toNMS
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import net.kyori.adventure.text.Component
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.Entity
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftLivingEntity
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

    private val itemsByIDMapField = SynchedEntityData::class.java
        .declaredFields
        .filter { it.type == Int2ObjectMap::class.java }
        .toList()[0]
        .apply { isAccessible = true }

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
        nmsEntity.isCustomNameVisible
        val entityData = SynchedEntityData(nmsEntity)

        entityData.forceSet(displayNameAccessor, Optional.of(nmsComponent))
        entityData.forceSet(customNameVisibleAccessor, visible)

        val packet = ClientboundSetEntityDataPacket(
            nmsEntity.id,
            entityData,
            true
        )

        player.sendPacket(Packet(packet))
    }

    private fun <T : Any> SynchedEntityData.forceSet(
        accessor: EntityDataAccessor<T>,
        value: T
    ) {
        if (!this.hasItem(accessor)) {
            this.define(accessor, value)
        }
        this[accessor] = value
        this.markDirty(accessor)
    }

    private fun <T : Any> SynchedEntityData.hasItem(accessor: EntityDataAccessor<T>): Boolean {
        val itemsByIDMap = itemsByIDMapField.get(this) as Int2ObjectMap<Any>
        return itemsByIDMap.containsKey(accessor.id)
    }
}
