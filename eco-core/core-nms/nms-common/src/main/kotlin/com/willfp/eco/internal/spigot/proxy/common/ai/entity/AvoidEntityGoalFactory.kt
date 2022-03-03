package com.willfp.eco.internal.spigot.proxy.common.ai.entity

import com.willfp.eco.core.entities.ai.entity.EntityGoalAvoidEntity
import com.willfp.eco.internal.spigot.proxy.common.ai.EntityGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.toBukkitEntity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal
import net.minecraft.world.entity.ai.goal.Goal

object AvoidEntityGoalFactory : EntityGoalFactory<EntityGoalAvoidEntity> {
    override fun create(apiGoal: EntityGoalAvoidEntity, entity: PathfinderMob): Goal {
        return AvoidEntityGoal(
            entity,
            LivingEntity::class.java,
            apiGoal.distance.toFloat(),
            apiGoal.slowSpeed,
            apiGoal.fastSpeed
        ) { apiGoal.entity.test(it.toBukkitEntity()) }
    }

    override fun isGoalOfType(goal: Goal) = goal is AvoidEntityGoal<*>
}
