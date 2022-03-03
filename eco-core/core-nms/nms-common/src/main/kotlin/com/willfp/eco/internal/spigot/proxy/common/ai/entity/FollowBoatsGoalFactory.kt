package com.willfp.eco.internal.spigot.proxy.common.ai.entity

import com.willfp.eco.core.entities.ai.entity.EntityGoalFollowBoats
import com.willfp.eco.internal.spigot.proxy.common.ai.EntityGoalFactory
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.FollowBoatGoal
import net.minecraft.world.entity.ai.goal.Goal

object FollowBoatsGoalFactory : EntityGoalFactory<EntityGoalFollowBoats> {
    override fun create(apiGoal: EntityGoalFollowBoats, entity: PathfinderMob): Goal {
        return FollowBoatGoal(
            entity
        )
    }

    override fun isGoalOfType(goal: Goal) = goal is FollowBoatGoal
}
