package com.willfp.eco.internal.spigot.proxy.common.ai.target

import com.willfp.eco.core.entities.ai.target.TargetGoalOwnerHurtBy
import com.willfp.eco.internal.spigot.proxy.common.ai.TargetGoalFactory
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal

object OwnerHurtByGoalFactory : TargetGoalFactory<TargetGoalOwnerHurtBy> {
    override fun create(apiGoal: TargetGoalOwnerHurtBy, entity: PathfinderMob): Goal? {
        return OwnerHurtByTargetGoal(
            entity as? TamableAnimal ?: return null
        )
    }

    override fun isGoalOfType(goal: Goal) = goal is OwnerHurtByTargetGoal
}
