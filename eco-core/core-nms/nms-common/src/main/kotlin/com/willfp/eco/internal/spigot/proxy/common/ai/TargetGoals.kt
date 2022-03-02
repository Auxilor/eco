package com.willfp.eco.internal.spigot.proxy.common.ai

import com.willfp.eco.core.entities.ai.CustomGoal
import com.willfp.eco.core.entities.ai.TargetGoal
import com.willfp.eco.core.entities.ai.target.TargetGoalHurtBy
import com.willfp.eco.core.entities.ai.target.TargetGoalNearestAttackable
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal

fun <T : TargetGoal<*>> T.getGoalFactory(): TargetGoalFactory<T>? {
    @Suppress("UNCHECKED_CAST")
    return when (this) {
        is TargetGoalHurtBy -> HurtByGoalFactory
        is TargetGoalNearestAttackable -> NearestAttackableGoalFactory
        is CustomGoal<*> -> CustomGoalFactory
        else -> null
    } as TargetGoalFactory<T>?
}

interface TargetGoalFactory<T : TargetGoal<*>> {
    fun create(apiGoal: T, entity: PathfinderMob): Goal?
}

object HurtByGoalFactory : TargetGoalFactory<TargetGoalHurtBy> {
    override fun create(apiGoal: TargetGoalHurtBy, entity: PathfinderMob): Goal {
        return HurtByTargetGoal(
            entity,
            *apiGoal.blacklist.map { it.toNMSClass() }.toTypedArray()
        )
    }
}

object NearestAttackableGoalFactory : TargetGoalFactory<TargetGoalNearestAttackable> {
    override fun create(apiGoal: TargetGoalNearestAttackable, entity: PathfinderMob): Goal {
        return NearestAttackableTargetGoal(
            entity,
            apiGoal.targetClass.toNMSClass(),
            apiGoal.checkVisibility
        )
    }
}
