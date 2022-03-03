package com.willfp.eco.internal.spigot.proxy.common.ai.entity

import com.willfp.eco.core.entities.ai.entity.EntityGoalFollowMobs
import com.willfp.eco.internal.spigot.proxy.common.ai.EntityGoalFactory
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.FollowMobGoal
import net.minecraft.world.entity.ai.goal.Goal

object FollowMobsGoalFactory : EntityGoalFactory<EntityGoalFollowMobs> {
    override fun create(apiGoal: EntityGoalFollowMobs, entity: PathfinderMob): Goal {
        return FollowMobGoal(
            entity,
            apiGoal.speed,
            apiGoal.minDistance.toFloat(),
            apiGoal.maxDistance.toFloat(),
        )
    }

    override fun isGoalOfType(goal: Goal) = goal is FollowMobGoal
}
