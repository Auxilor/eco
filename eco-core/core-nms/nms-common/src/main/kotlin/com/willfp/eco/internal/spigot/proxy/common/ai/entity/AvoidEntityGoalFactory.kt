package com.willfp.eco.internal.spigot.proxy.common.ai.entity

import com.willfp.eco.core.entities.ai.entity.EntityGoalAvoidEntity
import com.willfp.eco.internal.spigot.proxy.common.ai.EntityGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.toBukkitEntity
import com.willfp.eco.internal.spigot.proxy.common.ai.toNMSClass
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal
import net.minecraft.world.entity.ai.goal.Goal

object AvoidEntityGoalFactory : EntityGoalFactory<EntityGoalAvoidEntity> {
    override fun create(apiGoal: EntityGoalAvoidEntity, entity: PathfinderMob): Goal {
        return AvoidEntityGoal(
            entity,
            apiGoal.avoidClass.toNMSClass(),
            apiGoal.fleeDistance.toFloat(),
            apiGoal.slowSpeed,
            apiGoal.fastSpeed
        ) { apiGoal.filter.test(it.toBukkitEntity()) }
    }
}