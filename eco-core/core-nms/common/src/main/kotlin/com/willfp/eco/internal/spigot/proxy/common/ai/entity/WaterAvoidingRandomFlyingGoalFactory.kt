package com.willfp.eco.internal.spigot.proxy.common.ai.entity

import com.willfp.eco.core.entities.ai.entity.EntityGoalWaterAvoidingRandomFlying
import com.willfp.eco.internal.spigot.proxy.common.ai.EntityGoalFactory
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal

object WaterAvoidingRandomFlyingGoalFactory : EntityGoalFactory<EntityGoalWaterAvoidingRandomFlying> {
    override fun create(apiGoal: EntityGoalWaterAvoidingRandomFlying, entity: PathfinderMob): Goal {
        return WaterAvoidingRandomFlyingGoal(
            entity,
            apiGoal.speed
        )
    }

    override fun isGoalOfType(goal: Goal) = goal is WaterAvoidingRandomFlyingGoal
}
