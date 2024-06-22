package com.willfp.eco.internal.spigot.proxy.common.ai.entity

import com.willfp.eco.core.entities.ai.entity.EntityGoalMoveTowardsTarget
import com.willfp.eco.internal.spigot.proxy.common.ai.EntityGoalFactory
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal

object MoveTowardsTargetGoalFactory : EntityGoalFactory<EntityGoalMoveTowardsTarget> {
    override fun create(apiGoal: EntityGoalMoveTowardsTarget, entity: PathfinderMob): Goal {
        return MoveTowardsTargetGoal(
            entity,
            apiGoal.speed,
            apiGoal.maxDistance.toFloat()
        )
    }

    override fun isGoalOfType(goal: Goal) = goal is MoveTowardsTargetGoal
}
