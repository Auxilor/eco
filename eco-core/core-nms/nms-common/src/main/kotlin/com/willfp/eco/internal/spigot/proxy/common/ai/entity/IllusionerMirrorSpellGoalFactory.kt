package com.willfp.eco.internal.spigot.proxy.common.ai.entity

import com.willfp.eco.core.entities.ai.entity.EntityGoalIllusionerMirrorSpell
import com.willfp.eco.internal.spigot.proxy.common.ai.EntityGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.opengoals.IllusionerMirrorSpellGoal
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.monster.Illusioner

object IllusionerMirrorSpellGoalFactory : EntityGoalFactory<EntityGoalIllusionerMirrorSpell> {
    override fun create(apiGoal: EntityGoalIllusionerMirrorSpell, entity: PathfinderMob): Goal? {
        if (entity !is Illusioner) return null

        return IllusionerMirrorSpellGoal(
            entity
        )
    }

    override fun isGoalOfType(goal: Goal) = goal is IllusionerMirrorSpellGoal
            || goal::class.java.name.contains("IllusionerMirrorSpellGoal")
}
