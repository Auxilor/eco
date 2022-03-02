package com.willfp.eco.internal.spigot.proxy.common.ai.target

import com.willfp.eco.core.entities.ai.target.TargetGoalNearestHealableRaider
import com.willfp.eco.internal.spigot.proxy.common.ai.TargetGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.toBukkitEntity
import com.willfp.eco.internal.spigot.proxy.common.ai.toNMSClass
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.target.NearestHealableRaiderTargetGoal
import net.minecraft.world.entity.raid.Raider

object NearestHealableRaiderGoalFactory : TargetGoalFactory<TargetGoalNearestHealableRaider> {
    override fun create(apiGoal: TargetGoalNearestHealableRaider, entity: PathfinderMob): Goal? {
        return NearestHealableRaiderTargetGoal(
            entity as? Raider ?: return null,
            apiGoal.targetClass.toNMSClass(),
            apiGoal.checkVisibility,
        ) {
            apiGoal.targetFilter.test(it.toBukkitEntity())
        }
    }
}
