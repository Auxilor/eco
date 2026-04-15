package com.willfp.eco.internal.spigot.proxy.common.ai.entity

import com.willfp.eco.core.entities.ai.entity.EntityGoalIllusionerMirrorSpell
import com.willfp.eco.internal.spigot.proxy.common.ai.EntityGoalFactory
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.monster.Illusioner

object IllusionerMirrorSpellGoalFactory : EntityGoalFactory<EntityGoalIllusionerMirrorSpell> {
    private val mirrorSpellClass by lazy {
        Illusioner::class.java.declaredClasses
            .first { it.simpleName == "IllusionerMirrorSpellGoal" }
    }

    private val mirrorSpellConstructor by lazy {
        mirrorSpellClass
            .getDeclaredConstructor(Illusioner::class.java)
            .apply { isAccessible = true }
    }

    override fun create(apiGoal: EntityGoalIllusionerMirrorSpell, entity: PathfinderMob): Goal? {
        if (entity !is Illusioner) return null

        return mirrorSpellConstructor.newInstance(entity) as Goal
    }

    override fun isGoalOfType(goal: Goal): Boolean {
        return mirrorSpellClass.isInstance(goal)
    }
}
