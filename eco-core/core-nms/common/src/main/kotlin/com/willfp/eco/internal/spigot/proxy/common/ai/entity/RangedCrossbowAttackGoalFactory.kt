package com.willfp.eco.internal.spigot.proxy.common.ai.entity

import com.willfp.eco.core.entities.ai.entity.EntityGoalRangedCrossbowAttack
import com.willfp.eco.internal.spigot.proxy.common.ai.EntityGoalFactory
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.RangedCrossbowAttackGoal
import net.minecraft.world.entity.monster.CrossbowAttackMob
import net.minecraft.world.entity.monster.Monster
import net.minecraft.world.entity.monster.RangedAttackMob

object RangedCrossbowAttackGoalFactory : EntityGoalFactory<EntityGoalRangedCrossbowAttack> {
    override fun create(apiGoal: EntityGoalRangedCrossbowAttack, entity: PathfinderMob): Goal? {
        if (entity !is Monster) return null
        if (entity !is RangedAttackMob) return null
        if (entity !is CrossbowAttackMob) return null

        return RangedCrossbowAttackGoal(
            entity,
            apiGoal.speed,
            apiGoal.range.toFloat()
        )
    }

    override fun isGoalOfType(goal: Goal) = goal is RangedCrossbowAttackGoal<*>
}
