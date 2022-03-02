package com.willfp.eco.internal.spigot.proxy.common.ai.target

import com.willfp.eco.core.entities.ai.target.TargetGoalHurtBy
import com.willfp.eco.internal.spigot.proxy.common.ai.TargetGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.toNMSClass
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal

object HurtByGoalFactory : TargetGoalFactory<TargetGoalHurtBy> {
    override fun create(apiGoal: TargetGoalHurtBy, entity: PathfinderMob): Goal {
        return HurtByTargetGoal(
            entity,
            *apiGoal.blacklist.map { it.toNMSClass() }.toTypedArray()
        )
    }
}
