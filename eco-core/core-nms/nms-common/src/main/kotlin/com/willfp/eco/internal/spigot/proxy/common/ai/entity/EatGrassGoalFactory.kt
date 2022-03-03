package com.willfp.eco.internal.spigot.proxy.common.ai.entity

import com.willfp.eco.core.entities.ai.entity.EntityGoalEatGrass
import com.willfp.eco.internal.spigot.proxy.common.ai.EntityGoalFactory
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.EatBlockGoal
import net.minecraft.world.entity.ai.goal.Goal

object EatGrassGoalFactory : EntityGoalFactory<EntityGoalEatGrass> {
    override fun create(apiGoal: EntityGoalEatGrass, entity: PathfinderMob): Goal {
        return EatBlockGoal(
            entity
        )
    }

    override fun isGoalOfType(goal: Goal) = goal is EatBlockGoal
}
