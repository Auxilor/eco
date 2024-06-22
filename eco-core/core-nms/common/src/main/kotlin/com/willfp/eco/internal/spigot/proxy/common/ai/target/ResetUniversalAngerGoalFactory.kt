package com.willfp.eco.internal.spigot.proxy.common.ai.target

import com.willfp.eco.core.entities.ai.target.TargetGoalResetUniversalAnger
import com.willfp.eco.internal.spigot.proxy.common.ai.TargetGoalFactory
import net.minecraft.world.entity.NeutralMob
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal

object ResetUniversalAngerGoalFactory : TargetGoalFactory<TargetGoalResetUniversalAnger> {
    override fun create(apiGoal: TargetGoalResetUniversalAnger, entity: PathfinderMob): Goal? {
        if (entity !is NeutralMob) return null

        return ResetUniversalAngerTargetGoal(
            entity,
            apiGoal.triggerOthers
        )
    }

    override fun isGoalOfType(goal: Goal) = goal is ResetUniversalAngerTargetGoal<*>
}
