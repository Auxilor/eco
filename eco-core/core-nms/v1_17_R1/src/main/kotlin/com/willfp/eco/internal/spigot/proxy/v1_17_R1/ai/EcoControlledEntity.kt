package com.willfp.eco.internal.spigot.proxy.v1_17_R1.ai

import com.willfp.eco.core.entities.ai.ControlledEntity
import com.willfp.eco.core.entities.ai.goals.EntityGoal
import com.willfp.eco.core.entities.ai.goals.TargetGoal
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity
import org.bukkit.entity.Mob

class EcoControlledEntity(
    private val handle: Mob
) : ControlledEntity {
    override fun addEntityGoal(priority: Int, goal: EntityGoal): ControlledEntity {
        val craft = handle as? CraftEntity ?: return this
        val nms = craft.handle as? net.minecraft.world.entity.PathfinderMob ?: return this

        nms.goalSelector.addGoal(priority, goal.getImplementation().generateNMSGoal(goal, nms))

        return this
    }

    override fun addTargetGoal(priority: Int, goal: TargetGoal): ControlledEntity {
        val craft = handle as? CraftEntity ?: return this
        val nms = craft.handle as? net.minecraft.world.entity.PathfinderMob ?: return this

        nms.targetSelector.addGoal(priority, goal.getImplementation().generateNMSGoal(goal, nms))

        return this
    }

    override fun getEntity(): Mob {
        return handle
    }
}
