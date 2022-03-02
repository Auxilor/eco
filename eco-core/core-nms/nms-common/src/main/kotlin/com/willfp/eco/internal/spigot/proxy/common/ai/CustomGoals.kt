package com.willfp.eco.internal.spigot.proxy.common.ai

import com.willfp.eco.core.entities.ai.goals.CustomGoal
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal

object CustomGoalFactory : EntityGoalFactory<CustomGoal>, TargetGoalFactory<CustomGoal> {
    override fun create(apiGoal: CustomGoal, entity: PathfinderMob): Goal {
        return NMSCustomGoal(apiGoal, entity)
    }
}

private class NMSCustomGoal(
    private val customEntityGoal: CustomGoal,
    entity: PathfinderMob
) : Goal() {
    init {
        customEntityGoal.initialize(entity.bukkitMob)
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
