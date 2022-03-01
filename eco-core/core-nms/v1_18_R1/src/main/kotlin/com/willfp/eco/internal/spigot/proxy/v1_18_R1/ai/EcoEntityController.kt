package com.willfp.eco.internal.spigot.proxy.v1_18_R1.ai

import com.willfp.eco.core.entities.ai.EntityController
import com.willfp.eco.core.entities.ai.goals.EntityGoal
import com.willfp.eco.core.entities.ai.goals.TargetGoal
import net.minecraft.world.entity.PathfinderMob
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftEntity
import org.bukkit.entity.Mob

class EcoEntityController<T : Mob>(
    private val handle: T
) : EntityController<T> {
    override fun addEntityGoal(priority: Int, goal: EntityGoal): EntityController<T> {
        val nms = getNms() ?: return this

        nms.goalSelector.addGoal(
            priority,
            goal.getGoalFactory()?.create(goal, nms) ?: return this
        )

        return this
    }

    override fun removeEntityGoal(goal: EntityGoal): EntityController<T> {
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

    override fun addTargetGoal(priority: Int, goal: TargetGoal): EntityController<T> {
        val nms = getNms() ?: return this

        nms.targetSelector.addGoal(
            priority, goal.getGoalFactory()?.create(goal, nms) ?: return this
        )

        nms.targetSelector

        return this
    }

    override fun removeTargetGoal(goal: TargetGoal): EntityController<T> {
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
        val craft = handle as? CraftEntity ?: return null
        return craft.handle as? PathfinderMob
    }

    override fun getEntity(): T {
        return handle
    }
}
