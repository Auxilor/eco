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
import com.willfp.eco.core.entities.ai.goals.entity.EntityGoalLeapAtTarget
import com.willfp.eco.core.entities.ai.goals.entity.EntityGoalLookAtPlayer
import com.willfp.eco.core.entities.ai.goals.entity.EntityGoalMeleeAttack
import com.willfp.eco.core.entities.ai.goals.entity.EntityGoalMoveBackToVillage
import com.willfp.eco.core.entities.ai.goals.entity.EntityGoalMoveThroughVillage
import com.willfp.eco.core.entities.ai.goals.entity.EntityGoalMoveTowardsRestriction
import com.willfp.eco.core.entities.ai.goals.entity.EntityGoalMoveTowardsTarget
import com.willfp.eco.core.entities.ai.goals.entity.EntityGoalOcelotAttack
import com.willfp.eco.core.entities.ai.goals.entity.EntityGoalOpenDoors
import com.willfp.eco.core.entities.ai.goals.entity.EntityGoalPanic
import com.willfp.eco.core.entities.ai.goals.entity.EntityGoalRandomLookAround
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
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal
import net.minecraft.world.entity.ai.goal.MoveBackToVillageGoal
import net.minecraft.world.entity.ai.goal.MoveThroughVillageGoal
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal
import net.minecraft.world.entity.ai.goal.OcelotAttackGoal
import net.minecraft.world.entity.ai.goal.OpenDoorGoal
import net.minecraft.world.entity.ai.goal.PanicGoal
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal
import net.minecraft.world.entity.player.Player

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
        is EntityGoalLeapAtTarget -> LeapAtTargetImpl
        is EntityGoalLookAtPlayer -> LookAtPlayerImpl
        is EntityGoalMeleeAttack -> MeleeAttackImpl
        is EntityGoalMoveBackToVillage -> MoveBackToVillageImpl
        is EntityGoalMoveThroughVillage -> MoveThroughVillageImpl
        is EntityGoalMoveTowardsRestriction -> MoveTowardsRestrictionImpl
        is EntityGoalMoveTowardsTarget -> MoveTowardsTargetImpl
        is EntityGoalOcelotAttack -> OcelotAttackImpl
        is EntityGoalOpenDoors -> OpenDoorsImpl
        is EntityGoalPanic -> PanicImpl
        is EntityGoalRandomLookAround -> RandomLookAroundImpl
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

object LeapAtTargetImpl : EcoEntityGoal<EntityGoalLeapAtTarget> {
    override fun generateNMSGoal(apiGoal: EntityGoalLeapAtTarget, entity: PathfinderMob): Goal {
        return LeapAtTargetGoal(
            entity,
            apiGoal.velocity.toFloat()
        )
    }
}

object LookAtPlayerImpl : EcoEntityGoal<EntityGoalLookAtPlayer> {
    override fun generateNMSGoal(apiGoal: EntityGoalLookAtPlayer, entity: PathfinderMob): Goal {
        return LookAtPlayerGoal(
            entity,
            Player::class.java,
            apiGoal.range.toFloat(),
            apiGoal.chance.toFloat() / 100f,
        )
    }
}

object MeleeAttackImpl : EcoEntityGoal<EntityGoalMeleeAttack> {
    override fun generateNMSGoal(apiGoal: EntityGoalMeleeAttack, entity: PathfinderMob): Goal {
        return MeleeAttackGoal(
            entity,
            apiGoal.speed,
            apiGoal.pauseWhenMobIdle
        )
    }
}

object MoveBackToVillageImpl : EcoEntityGoal<EntityGoalMoveBackToVillage> {
    override fun generateNMSGoal(apiGoal: EntityGoalMoveBackToVillage, entity: PathfinderMob): Goal {
        return MoveBackToVillageGoal(
            entity,
            apiGoal.speed,
            apiGoal.canDespawn
        )
    }
}

object MoveThroughVillageImpl : EcoEntityGoal<EntityGoalMoveThroughVillage> {
    override fun generateNMSGoal(apiGoal: EntityGoalMoveThroughVillage, entity: PathfinderMob): Goal {
        return MoveThroughVillageGoal(
            entity,
            apiGoal.speed,
            apiGoal.onlyAtNight,
            apiGoal.distance,
            apiGoal.canPassThroughDoorsGetter
        )
    }
}

object MoveTowardsRestrictionImpl : EcoEntityGoal<EntityGoalMoveTowardsRestriction> {
    override fun generateNMSGoal(apiGoal: EntityGoalMoveTowardsRestriction, entity: PathfinderMob): Goal {
        return MoveTowardsRestrictionGoal(
            entity,
            apiGoal.speed
        )
    }
}

object MoveTowardsTargetImpl : EcoEntityGoal<EntityGoalMoveTowardsTarget> {
    override fun generateNMSGoal(apiGoal: EntityGoalMoveTowardsTarget, entity: PathfinderMob): Goal {
        return MoveTowardsTargetGoal(
            entity,
            apiGoal.speed,
            apiGoal.maxDistance.toFloat()
        )
    }
}

object OcelotAttackImpl : EcoEntityGoal<EntityGoalOcelotAttack> {
    override fun generateNMSGoal(apiGoal: EntityGoalOcelotAttack, entity: PathfinderMob): Goal {
        return OcelotAttackGoal(
            entity
        )
    }
}

object OpenDoorsImpl : EcoEntityGoal<EntityGoalOpenDoors> {
    override fun generateNMSGoal(apiGoal: EntityGoalOpenDoors, entity: PathfinderMob): Goal {
        return OpenDoorGoal(
            entity,
            apiGoal.delayedClose
        )
    }
}

object PanicImpl : EcoEntityGoal<EntityGoalPanic> {
    override fun generateNMSGoal(apiGoal: EntityGoalPanic, entity: PathfinderMob): Goal {
        return PanicGoal(
            entity,
            apiGoal.speed
        )
    }
}

object RandomLookAroundImpl : EcoEntityGoal<EntityGoalRandomLookAround> {
    override fun generateNMSGoal(apiGoal: EntityGoalRandomLookAround, entity: PathfinderMob): Goal {
        return RandomLookAroundGoal(
            entity
        )
    }
}
