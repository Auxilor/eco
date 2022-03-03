package com.willfp.eco.internal.spigot.proxy.common.ai.entity

import com.willfp.eco.core.entities.ai.entity.EntityGoalFleeSun
import com.willfp.eco.internal.spigot.proxy.common.ai.EntityGoalFactory
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.FleeSunGoal
import net.minecraft.world.entity.ai.goal.Goal

object FleeSunGoalFactory : EntityGoalFactory<EntityGoalFleeSun> {
    override fun create(apiGoal: EntityGoalFleeSun, entity: PathfinderMob): Goal {
        return FleeSunGoal(
            entity,
            apiGoal.speed
        )
    }

    override fun isGoalOfType(goal: Goal) = goal is FleeSunGoal
}
