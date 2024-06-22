package com.willfp.eco.internal.spigot.proxy.common.ai.entity

import com.willfp.eco.core.entities.ai.entity.EntityGoalLookAtPlayer
import com.willfp.eco.internal.spigot.proxy.common.ai.EntityGoalFactory
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
import net.minecraft.world.entity.player.Player

object LookAtPlayerGoalFactory : EntityGoalFactory<EntityGoalLookAtPlayer> {
    override fun create(apiGoal: EntityGoalLookAtPlayer, entity: PathfinderMob): Goal {
        return LookAtPlayerGoal(
            entity,
            Player::class.java,
            apiGoal.range.toFloat(),
            apiGoal.chance.toFloat(),
        )
    }

    override fun isGoalOfType(goal: Goal) = goal is LookAtPlayerGoal
}
