package com.willfp.eco.internal.spigot.proxy.common.ai.entity

import com.willfp.eco.core.entities.ai.entity.EntityGoalBreakDoor
import com.willfp.eco.internal.spigot.proxy.common.ai.EntityGoalFactory
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.BreakDoorGoal
import net.minecraft.world.entity.ai.goal.Goal

object BreakDoorGoalFactory : EntityGoalFactory<EntityGoalBreakDoor> {
    override fun create(apiGoal: EntityGoalBreakDoor, entity: PathfinderMob): Goal {
        return BreakDoorGoal(
            entity,
            apiGoal.maxProgress
        ) { true }
    }
}
