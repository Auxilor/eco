package com.willfp.eco.internal.spigot.proxy.v26_1_1.common.ai.entity

import com.willfp.eco.core.entities.ai.entity.EntityGoalFollowBoats
import com.willfp.eco.internal.spigot.proxy.common.ai.EntityGoalFactory
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.FollowPlayerRiddenEntityGoal
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.vehicle.boat.AbstractBoat

object FollowBoatsGoalFactory : EntityGoalFactory<EntityGoalFollowBoats> {
    override fun create(apiGoal: EntityGoalFollowBoats, entity: PathfinderMob): Goal {
        return FollowPlayerRiddenEntityGoal(
            entity,
            AbstractBoat::class.java
        )
    }

    override fun isGoalOfType(goal: Goal) = goal is FollowPlayerRiddenEntityGoal
}
