package com.willfp.eco.internal.spigot.proxy.common.ai.target

import com.willfp.eco.core.entities.ai.target.TargetGoalNearestHealableRaider
import com.willfp.eco.internal.spigot.proxy.common.ai.TargetGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.toBukkitEntity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.target.NearestHealableRaiderTargetGoal
import net.minecraft.world.entity.raid.Raider

object NearestHealableRaiderGoalFactory : TargetGoalFactory<TargetGoalNearestHealableRaider> {
    override fun create(apiGoal: TargetGoalNearestHealableRaider, entity: PathfinderMob): Goal? {
        return NearestHealableRaiderTargetGoal(
            entity as? Raider ?: return null,
            LivingEntity::class.java,
            apiGoal.checkVisibility,
        ) {
            apiGoal.targetFilter.test(it.toBukkitEntity()) && apiGoal.target.matches(it.toBukkitEntity())
        }
    }

    override fun isGoalOfType(goal: Goal) = goal is NearestHealableRaiderTargetGoal<*>
}
