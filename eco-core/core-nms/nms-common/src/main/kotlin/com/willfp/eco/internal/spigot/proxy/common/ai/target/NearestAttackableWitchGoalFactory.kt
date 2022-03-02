package com.willfp.eco.internal.spigot.proxy.common.ai.target

import com.willfp.eco.core.entities.ai.target.TargetGoalNearestAttackableWitch
import com.willfp.eco.internal.spigot.proxy.common.ai.TargetGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.toBukkitEntity
import com.willfp.eco.internal.spigot.proxy.common.ai.toNMSClass
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableWitchTargetGoal
import net.minecraft.world.entity.raid.Raider

object NearestAttackableWitchGoalFactory : TargetGoalFactory<TargetGoalNearestAttackableWitch> {
    override fun create(apiGoal: TargetGoalNearestAttackableWitch, entity: PathfinderMob): Goal? {
        return NearestAttackableWitchTargetGoal(
            entity as? Raider ?: return null,
            apiGoal.targetClass.toNMSClass(),
            apiGoal.reciprocalChance,
            apiGoal.checkVisibility,
            apiGoal.checkCanNavigate,
        ) {
            apiGoal.targetFilter.test(it.toBukkitEntity())
        }
    }
}
