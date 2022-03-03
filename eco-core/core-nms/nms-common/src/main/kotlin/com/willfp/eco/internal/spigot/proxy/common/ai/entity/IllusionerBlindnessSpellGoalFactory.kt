package com.willfp.eco.internal.spigot.proxy.common.ai.entity

import com.willfp.eco.core.entities.ai.entity.EntityGoalLeapAtTarget
import com.willfp.eco.internal.spigot.proxy.common.ai.EntityGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.opengoals.IllusionerBlindnessSpellGoal
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.monster.Illusioner

object IllusionerBlindnessSpellGoalFactory : EntityGoalFactory<EntityGoalLeapAtTarget> {
    override fun create(apiGoal: EntityGoalLeapAtTarget, entity: PathfinderMob): Goal? {
        if (entity !is Illusioner) return null

        return IllusionerBlindnessSpellGoal(
            entity
        )
    }

    override fun isGoalOfType(goal: Goal) = goal is IllusionerBlindnessSpellGoal
            || goal::class.java.name.contains("IllusionerBlindnessSpellGoal")
}
