package com.willfp.eco.internal.spigot.proxy.common.ai.target

import com.willfp.eco.core.entities.ai.target.TargetGoalNearestAttackable
import com.willfp.eco.internal.spigot.proxy.common.ai.TargetGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.toBukkitEntity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal

object NearestAttackableGoalFactory : TargetGoalFactory<TargetGoalNearestAttackable> {
    override fun create(apiGoal: TargetGoalNearestAttackable, entity: PathfinderMob): Goal {
        return NearestAttackableTargetGoal(
            entity,
            LivingEntity::class.java,
            apiGoal.reciprocalChance,
            apiGoal.checkVisibility,
            apiGoal.checkCanNavigate,
        ) {
            val bukkit = it.toBukkitEntity()

            apiGoal.targetFilter.test(bukkit) && apiGoal.targets.any { t -> t.matches(bukkit) }
        }
    }

    override fun isGoalOfType(goal: Goal) = goal is NearestAttackableTargetGoal<*>
}
