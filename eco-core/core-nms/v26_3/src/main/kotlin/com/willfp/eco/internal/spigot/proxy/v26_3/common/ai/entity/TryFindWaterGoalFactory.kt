package com.willfp.eco.internal.spigot.proxy.v26_3.common.ai.entity

import com.willfp.eco.core.entities.ai.entity.EntityGoalTryFindWater
import com.willfp.eco.internal.spigot.proxy.v26_2.common.ai.EntityGoalFactory
import net.minecraft.tags.FluidTags
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.TryFindLiquidGoal

object TryFindWaterGoalFactory : EntityGoalFactory<EntityGoalTryFindWater> {
    override fun create(apiGoal: EntityGoalTryFindWater, entity: PathfinderMob): Goal {
        return TryFindLiquidGoal(
            entity,
            FluidTags.WATER
        )
    }

    override fun isGoalOfType(goal: Goal) = goal is TryFindLiquidGoal
}
