package com.willfp.eco.internal.spigot.proxy.common.ai.entity

import com.willfp.eco.core.entities.ai.entity.EntityGoalTempt
import com.willfp.eco.internal.spigot.proxy.common.ai.EntityGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.asNMSStack
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.TemptGoal
import net.minecraft.world.item.crafting.Ingredient

object TemptGoalFactory : EntityGoalFactory<EntityGoalTempt> {
    override fun create(apiGoal: EntityGoalTempt, entity: PathfinderMob): Goal {
        return TemptGoal(
            entity,
            apiGoal.speed,
            Ingredient.of(*apiGoal.items.map { it.asNMSStack() }.toTypedArray()),
            apiGoal.canBeScared
        )
    }
}
