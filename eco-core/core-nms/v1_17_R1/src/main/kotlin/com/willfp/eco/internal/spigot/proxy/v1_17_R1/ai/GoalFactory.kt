package com.willfp.eco.internal.spigot.proxy.v1_17_R1.ai

import com.willfp.eco.core.entities.ai.goals.EntityGoal
import com.willfp.eco.core.entities.ai.goals.EntityGoalNearestAttackableTarget

object GoalFactory {
    fun <T : EntityGoal> getImplementation(apiGoal: T): EcoEntityGoal<T> {
        @Suppress("UNCHECKED_CAST")
        return when (apiGoal) {
            is EntityGoalNearestAttackableTarget -> NearestAttackableTargetImpl()
            else -> throw IllegalArgumentException("Unknown API goal!")
        } as EcoEntityGoal<T>
    }
}
