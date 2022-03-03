package com.willfp.eco.internal.spigot.proxy.common.ai.entity

import com.willfp.eco.core.entities.ai.entity.EntityGoalBreatheAir
import com.willfp.eco.internal.spigot.proxy.common.ai.EntityGoalFactory
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.BreathAirGoal
import net.minecraft.world.entity.ai.goal.Goal

object BreatheAirGoalFactory : EntityGoalFactory<EntityGoalBreatheAir> {
    override fun create(apiGoal: EntityGoalBreatheAir, entity: PathfinderMob): Goal {
        return BreathAirGoal(
            entity
        )
    }

    override fun isGoalOfType(goal: Goal) = goal is BreathAirGoal
}
