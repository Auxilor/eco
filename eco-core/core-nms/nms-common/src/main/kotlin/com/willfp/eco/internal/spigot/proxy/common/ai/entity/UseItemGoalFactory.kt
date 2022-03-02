package com.willfp.eco.internal.spigot.proxy.common.ai.entity

import com.willfp.eco.core.entities.ai.entity.EntityGoalUseItem
import com.willfp.eco.internal.spigot.proxy.common.ai.EntityGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.toBukkitEntity
import com.willfp.eco.internal.spigot.proxy.common.commonsProvider
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.UseItemGoal

object UseItemGoalFactory : EntityGoalFactory<EntityGoalUseItem> {
    override fun create(apiGoal: EntityGoalUseItem, entity: PathfinderMob): Goal {
        return UseItemGoal(
            entity,
            commonsProvider.asNMSStack(apiGoal.item),
            SoundEvent(commonsProvider.toResourceLocation(apiGoal.sound.key)),
        ) {
            apiGoal.condition.test(it.toBukkitEntity())
        }
    }
}
