package com.willfp.eco.internal.spigot.proxy.common.ai.entity

import com.willfp.eco.core.entities.ai.entity.EntityGoalIllusionerMirrorSpell
import com.willfp.eco.internal.spigot.proxy.common.ai.EntityGoalFactory
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.monster.Illusioner

object IllusionerMirrorSpellGoalFactory : EntityGoalFactory<EntityGoalIllusionerMirrorSpell> {
    override fun create(apiGoal: EntityGoalIllusionerMirrorSpell, entity: PathfinderMob): Goal? {
        if (entity !is Illusioner) return null

        // Have to use reflection for it to work
        return Illusioner::class.java.declaredClasses[0]
            .getDeclaredConstructor(Illusioner::class.java)
            .apply { isAccessible = true }
            .newInstance(entity) as Goal
    }

    override fun isGoalOfType(goal: Goal): Boolean {
        return Illusioner::class.java.declaredClasses[0].isInstance(goal)
    }
}
