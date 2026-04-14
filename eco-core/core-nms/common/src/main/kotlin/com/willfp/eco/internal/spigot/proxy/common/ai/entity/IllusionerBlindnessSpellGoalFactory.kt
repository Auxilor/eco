package com.willfp.eco.internal.spigot.proxy.common.ai.entity

import com.willfp.eco.core.entities.ai.entity.EntityGoalIllusionerBlindnessSpell
import com.willfp.eco.internal.spigot.proxy.common.ai.EntityGoalFactory
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.monster.Illusioner

object IllusionerBlindnessSpellGoalFactory : EntityGoalFactory<EntityGoalIllusionerBlindnessSpell> {
    private val blindnessSpellClass by lazy {
        Illusioner::class.java.declaredClasses
            .first { it.simpleName == "IllusionerBlindnessSpellGoal" }
    }

    private val blindnessSpellConstructor by lazy {
        blindnessSpellClass
            .getDeclaredConstructor(Illusioner::class.java)
            .apply { isAccessible = true }
    }

    override fun create(apiGoal: EntityGoalIllusionerBlindnessSpell, entity: PathfinderMob): Goal? {
        if (entity !is Illusioner) return null

        return blindnessSpellConstructor.newInstance(entity) as Goal
    }

    override fun isGoalOfType(goal: Goal): Boolean {
        return blindnessSpellClass.isInstance(goal)
    }
}
