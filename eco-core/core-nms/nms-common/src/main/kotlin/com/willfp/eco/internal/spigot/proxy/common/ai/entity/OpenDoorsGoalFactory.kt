package com.willfp.eco.internal.spigot.proxy.common.ai.entity

import com.willfp.eco.core.entities.ai.entity.EntityGoalOpenDoors
import com.willfp.eco.internal.spigot.proxy.common.ai.EntityGoalFactory
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.OpenDoorGoal

object OpenDoorsGoalFactory : EntityGoalFactory<EntityGoalOpenDoors> {
    override fun create(apiGoal: EntityGoalOpenDoors, entity: PathfinderMob): Goal {
        return OpenDoorGoal(
            entity,
            apiGoal.delayClosing
        )
    }

    override fun isGoalOfType(goal: Goal) = goal is OpenDoorGoal
}
