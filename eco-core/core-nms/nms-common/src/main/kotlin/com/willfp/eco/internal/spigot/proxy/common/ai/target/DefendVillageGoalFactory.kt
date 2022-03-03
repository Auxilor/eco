package com.willfp.eco.internal.spigot.proxy.common.ai.target

import com.willfp.eco.core.entities.ai.target.TargetGoalDefendVillage
import com.willfp.eco.internal.spigot.proxy.common.ai.TargetGoalFactory
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.target.DefendVillageTargetGoal
import net.minecraft.world.entity.animal.IronGolem

object DefendVillageGoalFactory : TargetGoalFactory<TargetGoalDefendVillage> {
    override fun create(apiGoal: TargetGoalDefendVillage, entity: PathfinderMob): Goal? {
        return DefendVillageTargetGoal(
            entity as? IronGolem ?: return null
        )
    }

    override fun isGoalOfType(goal: Goal) = goal is DefendVillageTargetGoal
}
