package com.willfp.eco.internal.spigot.proxy.common.ai.entity

import com.willfp.eco.core.entities.ai.entity.EntityGoalIllusionerBlindnessSpell
import com.willfp.eco.internal.spigot.proxy.common.ai.EntityGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.opengoals.IllusionerBlindnessSpellGoal
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.monster.Illusioner

object IllusionerBlindnessSpellGoalFactory : EntityGoalFactory<EntityGoalIllusionerBlindnessSpell> {
    override fun create(apiGoal: EntityGoalIllusionerBlindnessSpell, entity: PathfinderMob): Goal? {
        if (entity !is Illusioner) return null

        return IllusionerBlindnessSpellGoal(
            entity
        )
    }

    override fun isGoalOfType(goal: Goal) = goal is IllusionerBlindnessSpellGoal
            || goal::class.java.name.contains("IllusionerBlindnessSpellGoal")
}
