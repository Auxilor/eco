package com.willfp.eco.internal.spigot.proxy.common.ai.entity

import com.willfp.eco.core.entities.ai.entity.EntityGoalRandomLookAround
import com.willfp.eco.internal.spigot.proxy.common.ai.EntityGoalFactory
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal

object RandomLookAroundGoalFactory : EntityGoalFactory<EntityGoalRandomLookAround> {
    override fun create(apiGoal: EntityGoalRandomLookAround, entity: PathfinderMob): Goal {
        return RandomLookAroundGoal(
            entity
        )
    }

    override fun isGoalOfType(goal: Goal) = goal is RandomLookAroundGoal
}
