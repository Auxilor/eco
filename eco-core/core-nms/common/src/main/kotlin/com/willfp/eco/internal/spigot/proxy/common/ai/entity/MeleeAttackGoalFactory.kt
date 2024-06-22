package com.willfp.eco.internal.spigot.proxy.common.ai.entity

import com.willfp.eco.core.entities.ai.entity.EntityGoalMeleeAttack
import com.willfp.eco.internal.spigot.proxy.common.ai.EntityGoalFactory
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal

object MeleeAttackGoalFactory : EntityGoalFactory<EntityGoalMeleeAttack> {
    override fun create(apiGoal: EntityGoalMeleeAttack, entity: PathfinderMob): Goal {
        return MeleeAttackGoal(
            entity,
            apiGoal.speed,
            apiGoal.pauseWhenMobIdle
        )
    }

    override fun isGoalOfType(goal: Goal) = goal is MeleeAttackGoal
}
