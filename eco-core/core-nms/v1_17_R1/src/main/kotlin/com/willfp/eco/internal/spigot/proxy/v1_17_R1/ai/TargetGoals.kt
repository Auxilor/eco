package com.willfp.eco.internal.spigot.proxy.v1_17_R1.ai

import com.willfp.eco.core.entities.ai.goals.TargetGoal
import com.willfp.eco.core.entities.ai.goals.target.TargetGoalHurtBy
import com.willfp.eco.core.entities.ai.goals.target.TargetGoalNearestAttackable
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal

fun <T : TargetGoal> T.getImplementation(): EcoTargetGoal<T> {
    @Suppress("UNCHECKED_CAST")
    return when (this) {
        is TargetGoalHurtBy -> HurtByImpl
        is TargetGoalNearestAttackable -> NearestAttackableImpl
        else -> throw IllegalArgumentException("Unknown API goal!")
    } as EcoTargetGoal<T>
}

interface EcoTargetGoal<T : TargetGoal> {
    fun generateNMSGoal(apiGoal: T, entity: PathfinderMob): Goal?
}

object HurtByImpl : EcoTargetGoal<TargetGoalHurtBy> {
    override fun generateNMSGoal(apiGoal: TargetGoalHurtBy, entity: PathfinderMob): Goal {
        return HurtByTargetGoal(
            entity,
            *apiGoal.blacklist.map { it.toNMSClass() }.toTypedArray()
        )
    }
}

object NearestAttackableImpl : EcoTargetGoal<TargetGoalNearestAttackable> {
    override fun generateNMSGoal(apiGoal: TargetGoalNearestAttackable, entity: PathfinderMob): Goal {
        return NearestAttackableTargetGoal(
            entity,
            apiGoal.targetClass.toNMSClass(),
            apiGoal.checkVisibility
        )
    }
}
