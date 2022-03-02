package com.willfp.eco.internal.spigot.proxy.common.ai

import com.willfp.eco.core.entities.ai.EntityController
import com.willfp.eco.core.entities.ai.EntityGoal
import com.willfp.eco.core.entities.ai.TargetGoal
import com.willfp.eco.internal.spigot.proxy.common.commonsProvider
import net.minecraft.world.entity.PathfinderMob
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
        nms.goalSelector.removeGoal(
            goal.getGoalFactory()?.create(goal, nms) ?: return this
        )

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
        nms.targetSelector.removeGoal(
            goal.getGoalFactory()?.create(goal, nms) ?: return this
        )

        return this
    }

    override fun clearTargetGoals(): EntityController<T> {
        val nms = getNms() ?: return this
        nms.targetSelector.availableGoals.clear()
        return this
    }

    private fun getNms(): PathfinderMob? {
        return commonsProvider.toPathfinderMob(handle)
    }

    override fun getEntity(): T {
        return handle
    }
}
