package com.willfp.eco.internal.spigot.proxy.v1_21_8

import com.willfp.eco.internal.spigot.proxies.ItemUseProxy
import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.util.Vector

class ItemUse : ItemUseProxy {
    override fun useItemOn(
        player: Player,
        hand: EquipmentSlot,
        block: Block,
        face: BlockFace,
        clickedPosition: Vector?
    ): Boolean {
        val serverPlayer = (player as CraftPlayer).handle
        val interactionHand = if (hand == EquipmentSlot.OFF_HAND) InteractionHand.OFF_HAND else InteractionHand.MAIN_HAND
        val itemStack = serverPlayer.getItemInHand(interactionHand)

        if (itemStack.isEmpty || serverPlayer.cooldowns.isOnCooldown(itemStack)) {
            return false
        }

        val direction = Direction.valueOf(face.name)
        val position = BlockPos(block.x, block.y, block.z)
        val context = UseOnContext(
            serverPlayer,
            interactionHand,
            BlockHitResult(
                clickedPosition?.let { Vec3(block.x + it.x, block.y + it.y, block.z + it.z) }
                    ?: Vec3.atCenterOf(position).relative(direction, 0.5),
                direction,
                position,
                false
            )
        )
        val used = itemStack.copy()

        val result = if (serverPlayer.hasInfiniteMaterials()) {
            val count = itemStack.count
            itemStack.useOn(context).also { itemStack.count = count }
        } else {
            itemStack.useOn(context)
        }

        if (!result.consumesAction()) {
            return false
        }

        CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, position, used)
        return true
    }
}
