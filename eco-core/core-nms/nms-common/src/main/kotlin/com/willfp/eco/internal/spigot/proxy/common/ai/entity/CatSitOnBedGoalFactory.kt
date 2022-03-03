package com.willfp.eco.internal.spigot.proxy.common.ai.entity

import com.willfp.eco.core.entities.ai.entity.EntityGoalCatSitOnBed
import com.willfp.eco.internal.spigot.proxy.common.ai.EntityGoalFactory
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.CatSitOnBlockGoal
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.animal.Cat

object CatSitOnBedGoalFactory : EntityGoalFactory<EntityGoalCatSitOnBed> {
    override fun create(apiGoal: EntityGoalCatSitOnBed, entity: PathfinderMob): Goal? {
        return CatSitOnBlockGoal(
            entity as? Cat ?: return null,
            apiGoal.speed
        )
    }

    override fun isGoalOfType(goal: Goal) = goal is CatSitOnBlockGoal
}
