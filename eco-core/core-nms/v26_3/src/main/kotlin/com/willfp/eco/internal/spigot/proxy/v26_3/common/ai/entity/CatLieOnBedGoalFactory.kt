package com.willfp.eco.internal.spigot.proxy.v26_3.common.ai.entity

import com.willfp.eco.core.entities.ai.entity.EntityGoalCatLieOnBed
import com.willfp.eco.internal.spigot.proxy.v26_2.common.ai.EntityGoalFactory
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.CatLieOnBlockGoal
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.animal.feline.Cat

object CatLieOnBedGoalFactory : EntityGoalFactory<EntityGoalCatLieOnBed> {
    override fun create(apiGoal: EntityGoalCatLieOnBed, entity: PathfinderMob): Goal? {
        return CatLieOnBlockGoal(
            entity as? Cat ?: return null,
            apiGoal.speed,
            apiGoal.range
        )
    }

    override fun isGoalOfType(goal: Goal) = goal is CatLieOnBlockGoal
}
