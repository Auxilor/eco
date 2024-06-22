package com.willfp.eco.internal.spigot.proxy.common.ai.entity

import com.willfp.eco.core.entities.ai.entity.EntityGoalRandomStroll
import com.willfp.eco.internal.spigot.proxy.common.ai.EntityGoalFactory
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.RandomStrollGoal

object RandomStrollGoalFactory : EntityGoalFactory<EntityGoalRandomStroll> {
    override fun create(apiGoal: EntityGoalRandomStroll, entity: PathfinderMob): Goal {
        return RandomStrollGoal(
            entity,
            apiGoal.speed,
            apiGoal.interval,
            apiGoal.canDespawn
        )
    }

    override fun isGoalOfType(goal: Goal) = goal is RandomStrollGoal
}
