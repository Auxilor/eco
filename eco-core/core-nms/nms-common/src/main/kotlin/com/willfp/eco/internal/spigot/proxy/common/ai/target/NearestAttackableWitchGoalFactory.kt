package com.willfp.eco.internal.spigot.proxy.common.ai.target

import com.willfp.eco.core.entities.ai.target.TargetGoalNearestAttackableWitch
import com.willfp.eco.internal.spigot.proxy.common.ai.TargetGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.toBukkitEntity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableWitchTargetGoal
import net.minecraft.world.entity.raid.Raider

object NearestAttackableWitchGoalFactory : TargetGoalFactory<TargetGoalNearestAttackableWitch> {
    override fun create(apiGoal: TargetGoalNearestAttackableWitch, entity: PathfinderMob): Goal? {
        return NearestAttackableWitchTargetGoal(
            entity as? Raider ?: return null,
            LivingEntity::class.java,
            apiGoal.reciprocalChance,
            apiGoal.checkVisibility,
            apiGoal.checkCanNavigate,
        ) {
            apiGoal.targetFilter.test(it.toBukkitEntity()) && apiGoal.target.matches(it.toBukkitEntity())
        }
    }

    override fun isGoalOfType(goal: Goal) = goal is NearestAttackableTargetGoal<*>
}
