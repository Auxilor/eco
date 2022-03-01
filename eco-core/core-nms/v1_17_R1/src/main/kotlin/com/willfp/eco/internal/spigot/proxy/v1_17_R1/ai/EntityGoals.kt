package com.willfp.eco.internal.spigot.proxy.v1_17_R1.ai

import com.willfp.eco.core.entities.ai.goals.EntityGoal
import com.willfp.eco.core.entities.ai.goals.entity.EntityGoalAvoidEntity
import com.willfp.eco.core.entities.ai.goals.entity.EntityGoalBreakDoor
import com.willfp.eco.core.entities.ai.goals.entity.EntityGoalBreatheAir
import com.willfp.eco.core.entities.ai.goals.entity.EntityGoalEatBlock
import com.willfp.eco.core.entities.ai.goals.entity.EntityGoalFleeSun
import com.willfp.eco.core.entities.ai.goals.entity.EntityGoalFloat
import com.willfp.eco.core.entities.ai.goals.entity.EntityGoalFollowBoats
import com.willfp.eco.core.entities.ai.goals.entity.EntityGoalFollowMobs
import com.willfp.eco.core.entities.ai.goals.entity.EntityGoalInteract
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal
import net.minecraft.world.entity.ai.goal.BreakDoorGoal
import net.minecraft.world.entity.ai.goal.BreathAirGoal
import net.minecraft.world.entity.ai.goal.EatBlockGoal
import net.minecraft.world.entity.ai.goal.FleeSunGoal
import net.minecraft.world.entity.ai.goal.FloatGoal
import net.minecraft.world.entity.ai.goal.FollowBoatGoal
import net.minecraft.world.entity.ai.goal.FollowMobGoal
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.InteractGoal

fun <T : EntityGoal> T.getImplementation(): EcoEntityGoal<T> {
    @Suppress("UNCHECKED_CAST")
    return when (this) {
        is EntityGoalAvoidEntity -> AvoidEntityImpl
        is EntityGoalBreakDoor -> BreakDoorImpl
        is EntityGoalBreatheAir -> BreatheAirImpl
        is EntityGoalEatBlock -> EatBlockImpl
        is EntityGoalFleeSun -> FleeSunImpl
        is EntityGoalFloat -> FloatImpl
        is EntityGoalFollowBoats -> FollowBoatsImpl
        is EntityGoalFollowMobs -> FollowMobsImpl
        is EntityGoalInteract -> InteractImpl
        else -> throw IllegalArgumentException("Unknown API goal!")
    } as EcoEntityGoal<T>
}

interface EcoEntityGoal<T : EntityGoal> {
    fun generateNMSGoal(apiGoal: T, entity: PathfinderMob): Goal
}

object AvoidEntityImpl : EcoEntityGoal<EntityGoalAvoidEntity> {
    override fun generateNMSGoal(apiGoal: EntityGoalAvoidEntity, entity: PathfinderMob): Goal {
        return AvoidEntityGoal(
            entity,
            apiGoal.avoidClass.toNMSClass(),
            apiGoal.fleeDistance.toFloat(),
            apiGoal.slowSpeed,
            apiGoal.fastSpeed
        ) { apiGoal.filter.test(it.toBukkitEntity()) }
    }
}

object BreakDoorImpl : EcoEntityGoal<EntityGoalBreakDoor> {
    override fun generateNMSGoal(apiGoal: EntityGoalBreakDoor, entity: PathfinderMob): Goal {
        return BreakDoorGoal(
            entity,
            apiGoal.maxProgress
        ) { true }
    }
}

object BreatheAirImpl : EcoEntityGoal<EntityGoalBreatheAir> {
    override fun generateNMSGoal(apiGoal: EntityGoalBreatheAir, entity: PathfinderMob): Goal {
        return BreathAirGoal(
            entity
        )
    }
}

object EatBlockImpl : EcoEntityGoal<EntityGoalEatBlock> {
    override fun generateNMSGoal(apiGoal: EntityGoalEatBlock, entity: PathfinderMob): Goal {
        return EatBlockGoal(
            entity
        )
    }
}

object FleeSunImpl : EcoEntityGoal<EntityGoalFleeSun> {
    override fun generateNMSGoal(apiGoal: EntityGoalFleeSun, entity: PathfinderMob): Goal {
        return FleeSunGoal(
            entity,
            apiGoal.speed
        )
    }
}

object FloatImpl : EcoEntityGoal<EntityGoalFloat> {
    override fun generateNMSGoal(apiGoal: EntityGoalFloat, entity: PathfinderMob): Goal {
        return FloatGoal(
            entity
        )
    }
}

object FollowBoatsImpl : EcoEntityGoal<EntityGoalFollowBoats> {
    override fun generateNMSGoal(apiGoal: EntityGoalFollowBoats, entity: PathfinderMob): Goal {
        return FollowBoatGoal(
            entity
        )
    }
}

object FollowMobsImpl : EcoEntityGoal<EntityGoalFollowMobs> {
    override fun generateNMSGoal(apiGoal: EntityGoalFollowMobs, entity: PathfinderMob): Goal {
        return FollowMobGoal(
            entity,
            apiGoal.speed,
            apiGoal.minDistance.toFloat(),
            apiGoal.maxDistance.toFloat(),
        )
    }
}

object InteractImpl : EcoEntityGoal<EntityGoalInteract> {
    override fun generateNMSGoal(apiGoal: EntityGoalInteract, entity: PathfinderMob): Goal {
        return InteractGoal(
            entity,
            apiGoal.targetClass.toNMSClass(),
            apiGoal.range.toFloat(),
            apiGoal.chance.toFloat() / 100f,
        )
    }
}
