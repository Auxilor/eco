package com.willfp.eco.internal.spigot.proxy.v1_17_R1.ai

import com.willfp.eco.core.entities.ai.ControlledEntity
import com.willfp.eco.core.entities.ai.goals.EntityGoal
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity
import org.bukkit.entity.Mob

class EcoControlledEntity(
    private val handle: Mob
) : ControlledEntity {
    override fun addGoal(priority: Int, goal: EntityGoal): ControlledEntity {
        val craft = handle as? CraftEntity ?: return this
        val nms = craft.handle as? net.minecraft.world.entity.Mob ?: return this

        nms.goalSelector.addGoal(priority, GoalFactory.getImplementation(goal).generateNMSGoal(goal, nms))

        return this
    }

    override fun addTarget(priority: Int, goal: EntityGoal): ControlledEntity {
        val craft = handle as? CraftEntity ?: return this
        val nms = craft.handle as? net.minecraft.world.entity.Mob ?: return this

        nms.targetSelector.addGoal(priority, GoalFactory.getImplementation(goal).generateNMSGoal(goal, nms))

        return this
    }

    override fun getEntity(): Mob {
        return handle
    }
}
