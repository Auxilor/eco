package com.willfp.eco.internal.spigot.proxy.v1_21_5.entity

import com.willfp.eco.core.entities.ai.CustomGoal
import com.willfp.eco.core.entities.ai.EntityController
import com.willfp.eco.core.entities.ai.EntityGoal
import com.willfp.eco.core.entities.ai.TargetGoal
import com.willfp.eco.internal.spigot.proxy.common.ai.CustomGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.getGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.toPathfinderMob
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import org.bukkit.entity.Mob

class EcoEntityController<T : Mob>(
    private val handle: T
) : EntityController<T> {
    override fun addEntityGoal(priority: Int, goal: EntityGoal<in T>): EntityController<T> {
        val nms = getNms() ?: return this

        nms.goalSelector.addGoal(
            priority,
            goal.getGoalFactory()?.create(goal, nms) ?: return this
        )

        return this
    }

    override fun removeEntityGoal(goal: EntityGoal<in T>): EntityController<T> {
        val nms = getNms() ?: return this

        val predicate: (Goal) -> Boolean = if (goal is CustomGoal<*>) {
            { CustomGoalFactory.isGoalOfType(it, goal) }
        } else {
            { goal.getGoalFactory()?.isGoalOfType(it) == true }
        }

        for (wrapped in nms.goalSelector.availableGoals.toSet()) {
            if (predicate(wrapped.goal)) {
                nms.goalSelector.removeGoal(wrapped.goal)
            }
        }

        return this
    }

    override fun clearEntityGoals(): EntityController<T> {
        val nms = getNms() ?: return this
        nms.goalSelector.availableGoals.clear()
        return this
    }

    override fun addTargetGoal(priority: Int, goal: TargetGoal<in T>): EntityController<T> {
        val nms = getNms() ?: return this

        nms.targetSelector.addGoal(
            priority, goal.getGoalFactory()?.create(goal, nms) ?: return this
        )

        nms.targetSelector

        return this
    }

    override fun removeTargetGoal(goal: TargetGoal<in T>): EntityController<T> {
        val nms = getNms() ?: return this

        val predicate: (Goal) -> Boolean = if (goal is CustomGoal<*>) {
            { CustomGoalFactory.isGoalOfType(it, goal) }
        } else {
            { goal.getGoalFactory()?.isGoalOfType(it) == true }
        }

        for (wrapped in nms.targetSelector.availableGoals.toSet()) {
            if (predicate(wrapped.goal)) {
                nms.targetSelector.removeGoal(wrapped.goal)
            }
        }

        return this
    }

    override fun clearTargetGoals(): EntityController<T> {
        val nms = getNms() ?: return this
        nms.targetSelector.availableGoals.clear()
        return this
    }

    private fun getNms(): PathfinderMob? {
        return handle.toPathfinderMob()
    }

    override fun getEntity(): T {
        return handle
    }
}