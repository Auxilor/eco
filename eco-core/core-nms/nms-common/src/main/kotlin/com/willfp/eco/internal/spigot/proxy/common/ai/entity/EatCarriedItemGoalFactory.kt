package com.willfp.eco.internal.spigot.proxy.common.ai.entity

import com.willfp.eco.core.entities.ai.entity.EntityGoalEatCarriedItem
import com.willfp.eco.internal.spigot.proxy.common.ai.EntityGoalFactory
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.EatBlockGoal
import net.minecraft.world.entity.ai.goal.Goal

object EatCarriedItemGoalFactory : EntityGoalFactory<EntityGoalEatCarriedItem> {
    override fun create(apiGoal: EntityGoalEatCarriedItem, entity: PathfinderMob): Goal {
        return EatBlockGoal(
            entity
        )
    }
}
