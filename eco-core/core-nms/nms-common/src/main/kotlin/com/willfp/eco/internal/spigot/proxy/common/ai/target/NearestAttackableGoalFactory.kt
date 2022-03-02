package com.willfp.eco.internal.spigot.proxy.common.ai.target

import com.willfp.eco.core.entities.ai.target.TargetGoalNearestAttackable
import com.willfp.eco.internal.spigot.proxy.common.ai.TargetGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.toBukkitEntity
import com.willfp.eco.internal.spigot.proxy.common.ai.toNMSClass
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal

object NearestAttackableGoalFactory : TargetGoalFactory<TargetGoalNearestAttackable> {
    override fun create(apiGoal: TargetGoalNearestAttackable, entity: PathfinderMob): Goal {
        return NearestAttackableTargetGoal(
            entity,
            apiGoal.targetClass.toNMSClass(),
            apiGoal.reciprocalChance,
            apiGoal.checkVisibility,
            apiGoal.checkCanNavigate,
        ) {
            apiGoal.targetFilter.test(it.toBukkitEntity())
        }
    }
}
