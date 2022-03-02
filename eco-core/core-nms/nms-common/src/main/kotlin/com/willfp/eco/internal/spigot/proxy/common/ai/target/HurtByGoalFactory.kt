package com.willfp.eco.internal.spigot.proxy.common.ai.target

import com.willfp.eco.core.entities.TestableEntity
import com.willfp.eco.core.entities.ai.target.TargetGoalHurtBy
import com.willfp.eco.internal.spigot.proxy.common.ai.TargetGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.toBukkitEntity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
import net.minecraft.world.entity.ai.targeting.TargetingConditions

object HurtByGoalFactory : TargetGoalFactory<TargetGoalHurtBy> {
    override fun create(apiGoal: TargetGoalHurtBy, entity: PathfinderMob): Goal {
        return EnhancedHurtByTargetGoal(
            entity,
            apiGoal.blacklist
        )
    }
}

private class EnhancedHurtByTargetGoal(
    mob: PathfinderMob,
    private val blacklist: TestableEntity
) : HurtByTargetGoal(
    mob
) {
    override fun canAttack(target: LivingEntity?, targetPredicate: TargetingConditions): Boolean {
        if (blacklist.matches(target?.toBukkitEntity())) {
            return false
        }

        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        return super.canAttack(target, targetPredicate)
    }
}
