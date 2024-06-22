package com.willfp.eco.internal.spigot.proxy.common.ai.entity

import com.willfp.eco.core.entities.ai.entity.EntityGoalRestrictSun
import com.willfp.eco.internal.spigot.proxy.common.ai.EntityGoalFactory
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.RestrictSunGoal

object RestrictSunGoalFactory : EntityGoalFactory<EntityGoalRestrictSun> {
    override fun create(apiGoal: EntityGoalRestrictSun, entity: PathfinderMob): Goal {
        return RestrictSunGoal(
            entity
        )
    }

    override fun isGoalOfType(goal: Goal) = goal is RestrictSunGoal
}
