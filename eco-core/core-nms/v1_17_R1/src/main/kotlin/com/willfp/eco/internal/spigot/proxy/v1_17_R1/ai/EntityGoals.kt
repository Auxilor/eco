package com.willfp.eco.internal.spigot.proxy.v1_17_R1.ai

import com.willfp.eco.core.entities.ai.goals.EntityGoal
import com.willfp.eco.core.entities.ai.goals.EntityGoalNearestAttackableTarget
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal

interface EcoEntityGoal<T : EntityGoal> {
    fun generateNMSGoal(apiGoal: T, entity: Mob): Goal
}

class NearestAttackableTargetImpl : EcoEntityGoal<EntityGoalNearestAttackableTarget> {
    override fun generateNMSGoal(apiGoal: EntityGoalNearestAttackableTarget, entity: Mob): Goal {
        return NearestAttackableTargetGoal(entity, apiGoal.targetClass, apiGoal.checkVisibility)
    }
}
