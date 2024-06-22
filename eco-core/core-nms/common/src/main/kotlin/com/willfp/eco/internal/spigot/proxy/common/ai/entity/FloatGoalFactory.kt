package com.willfp.eco.internal.spigot.proxy.common.ai.entity

import com.willfp.eco.core.entities.ai.entity.EntityGoalFloat
import com.willfp.eco.internal.spigot.proxy.common.ai.EntityGoalFactory
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.FloatGoal
import net.minecraft.world.entity.ai.goal.Goal

object FloatGoalFactory : EntityGoalFactory<EntityGoalFloat> {
    override fun create(apiGoal: EntityGoalFloat, entity: PathfinderMob): Goal {
        return FloatGoal(
            entity
        )
    }

    override fun isGoalOfType(goal: Goal) = goal is FloatGoal
}
