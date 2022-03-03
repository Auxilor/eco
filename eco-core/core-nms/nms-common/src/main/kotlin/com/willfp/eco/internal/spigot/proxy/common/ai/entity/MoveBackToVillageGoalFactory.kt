package com.willfp.eco.internal.spigot.proxy.common.ai.entity

import com.willfp.eco.core.entities.ai.entity.EntityGoalMoveBackToVillage
import com.willfp.eco.internal.spigot.proxy.common.ai.EntityGoalFactory
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.MoveBackToVillageGoal

object MoveBackToVillageGoalFactory : EntityGoalFactory<EntityGoalMoveBackToVillage> {
    override fun create(apiGoal: EntityGoalMoveBackToVillage, entity: PathfinderMob): Goal {
        return MoveBackToVillageGoal(
            entity,
            apiGoal.speed,
            apiGoal.canDespawn
        )
    }

    override fun isGoalOfType(goal: Goal) = goal is MoveBackToVillageGoal
}
