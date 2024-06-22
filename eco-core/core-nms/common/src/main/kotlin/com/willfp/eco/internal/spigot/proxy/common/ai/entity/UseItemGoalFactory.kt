package com.willfp.eco.internal.spigot.proxy.common.ai.entity

import com.willfp.eco.core.entities.ai.entity.EntityGoalUseItem
import com.willfp.eco.internal.spigot.proxy.common.ai.EntityGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.asNMSStack
import com.willfp.eco.internal.spigot.proxy.common.toBukkitEntity
import com.willfp.eco.internal.spigot.proxy.common.toResourceLocation
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.UseItemGoal

object UseItemGoalFactory : EntityGoalFactory<EntityGoalUseItem> {
    override fun create(apiGoal: EntityGoalUseItem, entity: PathfinderMob): Goal {
        return UseItemGoal(
            entity,
            apiGoal.item.asNMSStack(),
            SoundEvent(apiGoal.sound.key.toResourceLocation()),
        ) {
            apiGoal.condition.test(it.toBukkitEntity())
        }
    }

    override fun isGoalOfType(goal: Goal) = goal is UseItemGoal<*>
}
