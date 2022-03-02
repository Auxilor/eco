package com.willfp.eco.internal.spigot.proxy.common.ai.entity

import com.willfp.eco.core.entities.ai.entity.EntityGoalInteract
import com.willfp.eco.internal.spigot.proxy.common.ai.EntityGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.toNMSClass
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.InteractGoal

object InteractGoalFactory : EntityGoalFactory<EntityGoalInteract> {
    override fun create(apiGoal: EntityGoalInteract, entity: PathfinderMob): Goal {
        return InteractGoal(
            entity,
            apiGoal.targetClass.toNMSClass(),
            apiGoal.range.toFloat(),
            apiGoal.chance.toFloat() / 100f,
        )
    }
}
