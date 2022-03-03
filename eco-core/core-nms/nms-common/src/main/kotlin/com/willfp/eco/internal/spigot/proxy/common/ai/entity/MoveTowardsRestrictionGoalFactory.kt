package com.willfp.eco.internal.spigot.proxy.common.ai.entity

import com.willfp.eco.core.entities.ai.entity.EntityGoalMoveTowardsRestriction
import com.willfp.eco.internal.spigot.proxy.common.ai.EntityGoalFactory
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal

object MoveTowardsRestrictionGoalFactory : EntityGoalFactory<EntityGoalMoveTowardsRestriction> {
    override fun create(apiGoal: EntityGoalMoveTowardsRestriction, entity: PathfinderMob): Goal {
        return MoveTowardsRestrictionGoal(
            entity,
            apiGoal.speed
        )
    }

    override fun isGoalOfType(goal: Goal) = goal is MoveTowardsRestrictionGoal
}
