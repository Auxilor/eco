package com.willfp.eco.internal.spigot.proxy.common.ai.entity

import com.willfp.eco.core.entities.ai.entity.EntityGoalStrollThroughVillage
import com.willfp.eco.internal.spigot.proxy.common.ai.EntityGoalFactory
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.StrollThroughVillageGoal

object StrollThroughVillageGoalFactory : EntityGoalFactory<EntityGoalStrollThroughVillage> {
    override fun create(apiGoal: EntityGoalStrollThroughVillage, entity: PathfinderMob): Goal {
        return StrollThroughVillageGoal(
            entity,
            apiGoal.searchRange
        )
    }

    override fun isGoalOfType(goal: Goal) = goal is StrollThroughVillageGoal
}
