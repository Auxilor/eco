package com.willfp.eco.internal.spigot.proxy.common.ai.entity

import com.willfp.eco.core.entities.ai.entity.EntityGoalRangedBowAttack
import com.willfp.eco.internal.spigot.proxy.common.ai.EntityGoalFactory
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal
import net.minecraft.world.entity.monster.Monster
import net.minecraft.world.entity.monster.RangedAttackMob

object RangedBowAttackGoalFactory : EntityGoalFactory<EntityGoalRangedBowAttack> {
    override fun create(apiGoal: EntityGoalRangedBowAttack, entity: PathfinderMob): Goal? {
        if (entity !is Monster) return null
        if (entity !is RangedAttackMob) return null

        return RangedBowAttackGoal(
            entity,
            apiGoal.speed,
            apiGoal.interval,
            apiGoal.range.toFloat()
        )
    }

    override fun isGoalOfType(goal: Goal) = goal is RangedBowAttackGoal<*>
}
