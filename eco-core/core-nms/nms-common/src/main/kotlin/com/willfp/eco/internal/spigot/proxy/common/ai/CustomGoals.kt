package com.willfp.eco.internal.spigot.proxy.common.ai

import com.willfp.eco.core.entities.ai.CustomGoal
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal

object CustomGoalFactory : EntityGoalFactory<CustomGoal<*>>, TargetGoalFactory<CustomGoal<*>> {
    override fun create(apiGoal: CustomGoal<*>, entity: PathfinderMob): Goal {
        return NMSCustomGoal(apiGoal, entity)
    }
}

private class NMSCustomGoal<T : org.bukkit.entity.Mob>(
    private val customEntityGoal: CustomGoal<T>,
    entity: PathfinderMob
) : Goal() {
    init {
        @Suppress("UNCHECKED_CAST")
        customEntityGoal.initialize(entity.bukkitMob as T)
    }

    override fun canUse(): Boolean {
        return customEntityGoal.canUse()
    }

    override fun tick() {
        customEntityGoal.tick()
    }

    override fun start() {
        customEntityGoal.start()
    }

    override fun stop() {
        customEntityGoal.stop()
    }
}
