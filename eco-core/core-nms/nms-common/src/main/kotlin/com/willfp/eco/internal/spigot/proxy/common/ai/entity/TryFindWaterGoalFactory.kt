package com.willfp.eco.internal.spigot.proxy.common.ai.entity

import com.willfp.eco.core.entities.ai.entity.EntityGoalTryFindWater
import com.willfp.eco.internal.spigot.proxy.common.ai.EntityGoalFactory
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal

object TryFindWaterGoalFactory : EntityGoalFactory<EntityGoalTryFindWater> {
    override fun create(apiGoal: EntityGoalTryFindWater, entity: PathfinderMob): Goal {
        return TryFindWaterGoal(
            entity
        )
    }

    override fun isGoalOfType(goal: Goal) = goal is TryFindWaterGoal
}
