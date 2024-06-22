package com.willfp.eco.internal.spigot.proxy.common.ai.entity

import com.willfp.eco.core.entities.ai.entity.EntityGoalPanic
import com.willfp.eco.internal.spigot.proxy.common.ai.EntityGoalFactory
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.PanicGoal

object PanicGoalFactory : EntityGoalFactory<EntityGoalPanic> {
    override fun create(apiGoal: EntityGoalPanic, entity: PathfinderMob): Goal {
        return PanicGoal(
            entity,
            apiGoal.speed
        )
    }

    override fun isGoalOfType(goal: Goal) = goal is PanicGoal
}
