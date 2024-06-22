package com.willfp.eco.internal.spigot.proxy.common.ai

import com.willfp.eco.core.entities.ai.CustomGoal
import com.willfp.eco.core.entities.ai.TargetGoal
import com.willfp.eco.core.entities.ai.target.TargetGoalHurtBy
import com.willfp.eco.core.entities.ai.target.TargetGoalNearestAttackable
import com.willfp.eco.core.entities.ai.target.TargetGoalNearestAttackableWitch
import com.willfp.eco.core.entities.ai.target.TargetGoalNearestHealableRaider
import com.willfp.eco.core.entities.ai.target.TargetGoalNonTameRandom
import com.willfp.eco.core.entities.ai.target.TargetGoalOwnerHurtBy
import com.willfp.eco.core.entities.ai.target.TargetGoalOwnerTarget
import com.willfp.eco.internal.spigot.proxy.common.ai.target.HurtByGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.target.NearestAttackableGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.target.NearestAttackableWitchGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.target.NearestHealableRaiderGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.target.NonTameRandomGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.target.OwnerHurtByGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.target.OwnerTargetGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.getVersionSpecificEntityGoalFactory
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal

fun <T : TargetGoal<*>> T.getGoalFactory(): TargetGoalFactory<T>? {
    val versionSpecific = this.getVersionSpecificEntityGoalFactory()
    if (versionSpecific != null) {
        return versionSpecific
    }

    @Suppress("UNCHECKED_CAST")
    return when (this) {
        is TargetGoalHurtBy -> HurtByGoalFactory
        is TargetGoalNearestAttackable -> NearestAttackableGoalFactory
        is TargetGoalNearestAttackableWitch -> NearestAttackableWitchGoalFactory
        is TargetGoalNearestHealableRaider -> NearestHealableRaiderGoalFactory
        is TargetGoalNonTameRandom -> NonTameRandomGoalFactory
        is TargetGoalOwnerHurtBy -> OwnerHurtByGoalFactory
        is TargetGoalOwnerTarget -> OwnerTargetGoalFactory
        is CustomGoal<*> -> CustomGoalFactory
        else -> null
    } as TargetGoalFactory<T>?
}

interface TargetGoalFactory<T : TargetGoal<*>> {
    fun create(apiGoal: T, entity: PathfinderMob): Goal?
    fun isGoalOfType(goal: Goal): Boolean
}
