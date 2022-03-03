package com.willfp.eco.internal.spigot.proxy.common.ai.target

import com.willfp.eco.core.entities.ai.target.TargetGoalNonTameRandom
import com.willfp.eco.internal.spigot.proxy.common.ai.TargetGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.toBukkitEntity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal

object NonTameRandomGoalFactory : TargetGoalFactory<TargetGoalNonTameRandom> {
    override fun create(apiGoal: TargetGoalNonTameRandom, entity: PathfinderMob): Goal? {
        return NonTameRandomTargetGoal(
            entity as? TamableAnimal ?: return null,
            LivingEntity::class.java,
            apiGoal.checkVisibility,
        ) {
            apiGoal.targetFilter.test(it.toBukkitEntity()) && apiGoal.target.matches(it.toBukkitEntity())
        }
    }

    override fun isGoalOfType(goal: Goal) = goal is NonTameRandomTargetGoal<*>
}
