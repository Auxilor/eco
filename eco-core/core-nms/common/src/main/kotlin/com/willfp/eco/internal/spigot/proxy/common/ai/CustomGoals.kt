package com.willfp.eco.internal.spigot.proxy.common.ai

import com.willfp.eco.core.entities.ai.CustomGoal
import com.willfp.eco.core.entities.ai.GoalFlag
import com.willfp.eco.internal.spigot.proxy.common.toBukkitEntity
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import java.util.EnumSet

object CustomGoalFactory : EntityGoalFactory<CustomGoal<*>>, TargetGoalFactory<CustomGoal<*>> {
    override fun create(apiGoal: CustomGoal<*>, entity: PathfinderMob): Goal {
        return NMSCustomGoal(apiGoal, entity)
    }

    override fun isGoalOfType(goal: Goal): Boolean = goal is NMSCustomGoal<*>

    fun isGoalOfType(goal: Goal, apiGoal: CustomGoal<*>): Boolean {
        if (goal !is NMSCustomGoal<*>) return false

        // ew
        return goal.customEntityGoal::class.java.name == apiGoal::class.java.name
    }
}

private fun Collection<Goal.Flag>.toEcoFlags(): Collection<GoalFlag> {
    return this.mapNotNull {
        when (it) {
            Goal.Flag.JUMP -> GoalFlag.JUMP
            Goal.Flag.LOOK -> GoalFlag.LOOK
            Goal.Flag.MOVE -> GoalFlag.MOVE
            Goal.Flag.TARGET -> GoalFlag.TARGET
            else -> null
        }
    }
}

private fun Collection<GoalFlag>.toNMSFlags(): Collection<Goal.Flag> {
    return this.map {
        when (it) {
            GoalFlag.JUMP -> Goal.Flag.JUMP
            GoalFlag.LOOK -> Goal.Flag.LOOK
            GoalFlag.MOVE -> Goal.Flag.MOVE
            GoalFlag.TARGET -> Goal.Flag.TARGET
        }
    }
}

private class NMSCustomGoal<T : org.bukkit.entity.Mob>(
    val customEntityGoal: CustomGoal<T>,
    entity: PathfinderMob
) : Goal() {
    init {
        @Suppress("UNCHECKED_CAST")
        customEntityGoal.initialize(entity.toBukkitEntity() as T)
        this.setFlags(EnumSet.copyOf(customEntityGoal.flags.toNMSFlags()))
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

    override fun canContinueToUse(): Boolean {
        return customEntityGoal.canContinueToUse()
    }

    override fun isInterruptable(): Boolean {
        return customEntityGoal.isInterruptable
    }

    override fun setFlags(controls: EnumSet<Flag>) {
        super.setFlags(controls)
        customEntityGoal.flags = EnumSet.copyOf(controls.toEcoFlags())
    }
}
