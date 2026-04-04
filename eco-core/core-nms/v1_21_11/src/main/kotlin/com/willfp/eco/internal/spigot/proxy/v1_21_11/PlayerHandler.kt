package com.willfp.eco.internal.spigot.proxy.v1_21_11

import com.willfp.eco.internal.spigot.proxies.PlayerHandlerProxy
import com.willfp.eco.internal.spigot.proxy.common.toNMS
import net.minecraft.core.component.DataComponents
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents
import net.minecraft.world.item.enchantment.EnchantmentHelper
import org.bukkit.craftbukkit.event.CraftEventFactory
import org.bukkit.entity.ExperienceOrb
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityRemoveEvent

class PlayerHandler : PlayerHandlerProxy {
    override fun giveExpAndApplyMending(player: Player, amount: Int, applyMending: Boolean) {
        var finalExp = amount
        if (applyMending) {
            finalExp = player.applyMending(amount)
        }
        player.toNMS().giveExperiencePoints(finalExp)
    }

    override fun Player.applyMending(amount: Int): Int {
        var finalAmount = amount
        val handle = this.toNMS()
        val stackEntry = EnchantmentHelper.getRandomItemWith(
            EnchantmentEffectComponents.REPAIR_WITH_XP,
            handle,
            ItemStack::isDamaged
        )
        val itemStack = stackEntry.map { it.itemStack }.orElse(ItemStack.EMPTY)
        if (!itemStack.isEmpty && itemStack.item.components()
                .has(DataComponents.MAX_DAMAGE)
        ) {
            val orb = EntityType.EXPERIENCE_ORB.create(handle.level(), EntitySpawnReason.COMMAND)
            orb?.value = finalAmount
            orb!!.spawnReason = ExperienceOrb.SpawnReason.CUSTOM
            orb.setPosRaw(handle.x, handle.y, handle.z)
            val possibleDurabilityFromXp = EnchantmentHelper.modifyDurabilityToRepairFromXp(
                handle.level(), itemStack, amount
            )
            var i = possibleDurabilityFromXp.coerceAtMost(itemStack.damageValue)
            val consumedExperience = if (i > 0) i * amount / possibleDurabilityFromXp else possibleDurabilityFromXp
            val event = CraftEventFactory.callPlayerItemMendEvent(
                handle,
                orb,
                itemStack,
                stackEntry.get().inSlot(),
                i,
                consumedExperience
            )
            i = event.repairAmount
            orb.discard(EntityRemoveEvent.Cause.DESPAWN)
            if (!event.isCancelled) {
                finalAmount -= consumedExperience // Use previously computed variable to reduce diff on change.
                itemStack.damageValue -= i
            }
        }
        return finalAmount
    }
}