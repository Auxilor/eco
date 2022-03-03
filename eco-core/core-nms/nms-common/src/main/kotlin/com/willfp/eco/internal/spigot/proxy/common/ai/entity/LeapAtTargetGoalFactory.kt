package com.willfp.eco.internal.spigot.proxy.common.ai.entity

import com.willfp.eco.core.entities.ai.entity.EntityGoalLeapAtTarget
import com.willfp.eco.internal.spigot.proxy.common.ai.EntityGoalFactory
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal

object LeapAtTargetGoalFactory : EntityGoalFactory<EntityGoalLeapAtTarget> {
    override fun create(apiGoal: EntityGoalLeapAtTarget, entity: PathfinderMob): Goal {
        return LeapAtTargetGoal(
            entity,
            apiGoal.velocity.toFloat()
        )
    }

    override fun isGoalOfType(goal: Goal) = goal is LeapAtTargetGoal
}
