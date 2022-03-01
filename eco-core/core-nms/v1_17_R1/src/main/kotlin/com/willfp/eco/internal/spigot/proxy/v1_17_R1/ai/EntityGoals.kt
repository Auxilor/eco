package com.willfp.eco.internal.spigot.proxy.v1_17_R1.ai

import com.willfp.eco.core.entities.ai.goals.CustomGoal
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
import com.willfp.eco.core.entities.ai.goals.entity.EntityGoalRandomStroll
import com.willfp.eco.core.entities.ai.goals.entity.EntityGoalRandomSwimming
import com.willfp.eco.core.entities.ai.goals.entity.EntityGoalRangedAttack
import com.willfp.eco.core.entities.ai.goals.entity.EntityGoalRangedBowAttack
import com.willfp.eco.core.entities.ai.goals.entity.EntityGoalRangedCrossbowAttack
import com.willfp.eco.core.entities.ai.goals.entity.EntityGoalRestrictSun
import com.willfp.eco.core.entities.ai.goals.entity.EntityGoalStrollThroughVillage
import com.willfp.eco.core.entities.ai.goals.entity.EntityGoalTempt
import com.willfp.eco.core.entities.ai.goals.entity.EntityGoalTryFindWater
import com.willfp.eco.core.entities.ai.goals.entity.EntityGoalUseItem
import com.willfp.eco.core.entities.ai.goals.entity.EntityGoalWaterAvoidingRandomFlying
import com.willfp.eco.core.entities.ai.goals.entity.EntityGoalWaterAvoidingRandomStroll
import net.minecraft.sounds.SoundEvent
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
import net.minecraft.world.entity.ai.goal.RandomStrollGoal
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal
import net.minecraft.world.entity.ai.goal.RangedAttackGoal
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal
import net.minecraft.world.entity.ai.goal.RangedCrossbowAttackGoal
import net.minecraft.world.entity.ai.goal.RestrictSunGoal
import net.minecraft.world.entity.ai.goal.StrollThroughVillageGoal
import net.minecraft.world.entity.ai.goal.TemptGoal
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal
import net.minecraft.world.entity.ai.goal.UseItemGoal
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal
import net.minecraft.world.entity.monster.CrossbowAttackMob
import net.minecraft.world.entity.monster.Monster
import net.minecraft.world.entity.monster.RangedAttackMob
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.crafting.Ingredient
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack
import org.bukkit.craftbukkit.v1_17_R1.util.CraftNamespacedKey

fun <T : EntityGoal> T.getGoalFactory(): EntityGoalFactory<T>? {
    @Suppress("UNCHECKED_CAST")
    return when (this) {
        is EntityGoalAvoidEntity -> AvoidEntityGoalFactory
        is EntityGoalBreakDoor -> BreakDoorGoalFactory
        is EntityGoalBreatheAir -> BreatheAirGoalFactory
        is EntityGoalEatBlock -> EatBlockGoalFactory
        is EntityGoalFleeSun -> FleeSunGoalFactory
        is EntityGoalFloat -> FloatGoalFactory
        is EntityGoalFollowBoats -> FollowBoatsGoalFactory
        is EntityGoalFollowMobs -> FollowMobsGoalFactory
        is EntityGoalInteract -> InteractGoalFactory
        is EntityGoalLeapAtTarget -> LeapAtTargetGoalFactory
        is EntityGoalLookAtPlayer -> LookAtPlayerGoalFactory
        is EntityGoalMeleeAttack -> MeleeAttackGoalFactory
        is EntityGoalMoveBackToVillage -> MoveBackToVillageGoalFactory
        is EntityGoalMoveThroughVillage -> MoveThroughVillageGoalFactory
        is EntityGoalMoveTowardsRestriction -> MoveTowardsRestrictionGoalFactory
        is EntityGoalMoveTowardsTarget -> MoveTowardsTargetGoalFactory
        is EntityGoalOcelotAttack -> OcelotAttackGoalFactory
        is EntityGoalOpenDoors -> OpenDoorsGoalFactory
        is EntityGoalPanic -> PanicGoalFactory
        is EntityGoalRandomLookAround -> RandomLookAroundGoalFactory
        is EntityGoalRandomStroll -> RandomStrollGoalFactory
        is EntityGoalRandomSwimming -> RandomSwimmingGoalFactory
        is EntityGoalRangedAttack -> RangedAttackGoalFactory
        is EntityGoalRangedBowAttack -> RangedBowAttackGoalFactory
        is EntityGoalRangedCrossbowAttack -> RangedCrossbowAttackGoalFactory
        is EntityGoalRestrictSun -> RestrictSunGoalFactory
        is EntityGoalStrollThroughVillage -> StrollThroughVillageGoalFactory
        is EntityGoalTempt -> TemptGoalFactory
        is EntityGoalTryFindWater -> TryFindWaterGoalFactory
        is EntityGoalUseItem -> UseItemGoalFactory
        is EntityGoalWaterAvoidingRandomFlying -> WaterAvoidingRandomFlyingGoalFactory
        is EntityGoalWaterAvoidingRandomStroll -> WaterAvoidingRandomStrollGoalFactory
        is CustomGoal -> CustomGoalFactory
        else -> null
    } as EntityGoalFactory<T>?
}

interface EntityGoalFactory<T : EntityGoal> {
    fun create(apiGoal: T, entity: PathfinderMob): Goal?
}

object AvoidEntityGoalFactory : EntityGoalFactory<EntityGoalAvoidEntity> {
    override fun create(apiGoal: EntityGoalAvoidEntity, entity: PathfinderMob): Goal {
        return AvoidEntityGoal(
            entity,
            apiGoal.avoidClass.toNMSClass(),
            apiGoal.fleeDistance.toFloat(),
            apiGoal.slowSpeed,
            apiGoal.fastSpeed
        ) { apiGoal.filter.test(it.toBukkitEntity()) }
    }
}

object BreakDoorGoalFactory : EntityGoalFactory<EntityGoalBreakDoor> {
    override fun create(apiGoal: EntityGoalBreakDoor, entity: PathfinderMob): Goal {
        return BreakDoorGoal(
            entity,
            apiGoal.maxProgress
        ) { true }
    }
}

object BreatheAirGoalFactory : EntityGoalFactory<EntityGoalBreatheAir> {
    override fun create(apiGoal: EntityGoalBreatheAir, entity: PathfinderMob): Goal {
        return BreathAirGoal(
            entity
        )
    }
}

object EatBlockGoalFactory : EntityGoalFactory<EntityGoalEatBlock> {
    override fun create(apiGoal: EntityGoalEatBlock, entity: PathfinderMob): Goal {
        return EatBlockGoal(
            entity
        )
    }
}

object FleeSunGoalFactory : EntityGoalFactory<EntityGoalFleeSun> {
    override fun create(apiGoal: EntityGoalFleeSun, entity: PathfinderMob): Goal {
        return FleeSunGoal(
            entity,
            apiGoal.speed
        )
    }
}

object FloatGoalFactory : EntityGoalFactory<EntityGoalFloat> {
    override fun create(apiGoal: EntityGoalFloat, entity: PathfinderMob): Goal {
        return FloatGoal(
            entity
        )
    }
}

object FollowBoatsGoalFactory : EntityGoalFactory<EntityGoalFollowBoats> {
    override fun create(apiGoal: EntityGoalFollowBoats, entity: PathfinderMob): Goal {
        return FollowBoatGoal(
            entity
        )
    }
}

object FollowMobsGoalFactory : EntityGoalFactory<EntityGoalFollowMobs> {
    override fun create(apiGoal: EntityGoalFollowMobs, entity: PathfinderMob): Goal {
        return FollowMobGoal(
            entity,
            apiGoal.speed,
            apiGoal.minDistance.toFloat(),
            apiGoal.maxDistance.toFloat(),
        )
    }
}

object InteractGoalFactory : EntityGoalFactory<EntityGoalInteract> {
    override fun create(apiGoal: EntityGoalInteract, entity: PathfinderMob): Goal {
        return InteractGoal(
            entity,
            apiGoal.targetClass.toNMSClass(),
            apiGoal.range.toFloat(),
            apiGoal.chance.toFloat() / 100f,
        )
    }
}

object LeapAtTargetGoalFactory : EntityGoalFactory<EntityGoalLeapAtTarget> {
    override fun create(apiGoal: EntityGoalLeapAtTarget, entity: PathfinderMob): Goal {
        return LeapAtTargetGoal(
            entity,
            apiGoal.velocity.toFloat()
        )
    }
}

object LookAtPlayerGoalFactory : EntityGoalFactory<EntityGoalLookAtPlayer> {
    override fun create(apiGoal: EntityGoalLookAtPlayer, entity: PathfinderMob): Goal {
        return LookAtPlayerGoal(
            entity,
            Player::class.java,
            apiGoal.range.toFloat(),
            apiGoal.chance.toFloat() / 100f,
        )
    }
}

object MeleeAttackGoalFactory : EntityGoalFactory<EntityGoalMeleeAttack> {
    override fun create(apiGoal: EntityGoalMeleeAttack, entity: PathfinderMob): Goal {
        return MeleeAttackGoal(
            entity,
            apiGoal.speed,
            apiGoal.pauseWhenMobIdle
        )
    }
}

object MoveBackToVillageGoalFactory : EntityGoalFactory<EntityGoalMoveBackToVillage> {
    override fun create(apiGoal: EntityGoalMoveBackToVillage, entity: PathfinderMob): Goal {
        return MoveBackToVillageGoal(
            entity,
            apiGoal.speed,
            apiGoal.canDespawn
        )
    }
}

object MoveThroughVillageGoalFactory : EntityGoalFactory<EntityGoalMoveThroughVillage> {
    override fun create(apiGoal: EntityGoalMoveThroughVillage, entity: PathfinderMob): Goal {
        return MoveThroughVillageGoal(
            entity,
            apiGoal.speed,
            apiGoal.onlyAtNight,
            apiGoal.distance,
            apiGoal.canPassThroughDoorsGetter
        )
    }
}

object MoveTowardsRestrictionGoalFactory : EntityGoalFactory<EntityGoalMoveTowardsRestriction> {
    override fun create(apiGoal: EntityGoalMoveTowardsRestriction, entity: PathfinderMob): Goal {
        return MoveTowardsRestrictionGoal(
            entity,
            apiGoal.speed
        )
    }
}

object MoveTowardsTargetGoalFactory : EntityGoalFactory<EntityGoalMoveTowardsTarget> {
    override fun create(apiGoal: EntityGoalMoveTowardsTarget, entity: PathfinderMob): Goal {
        return MoveTowardsTargetGoal(
            entity,
            apiGoal.speed,
            apiGoal.maxDistance.toFloat()
        )
    }
}

object OcelotAttackGoalFactory : EntityGoalFactory<EntityGoalOcelotAttack> {
    override fun create(apiGoal: EntityGoalOcelotAttack, entity: PathfinderMob): Goal {
        return OcelotAttackGoal(
            entity
        )
    }
}

object OpenDoorsGoalFactory : EntityGoalFactory<EntityGoalOpenDoors> {
    override fun create(apiGoal: EntityGoalOpenDoors, entity: PathfinderMob): Goal {
        return OpenDoorGoal(
            entity,
            apiGoal.delayedClose
        )
    }
}

object PanicGoalFactory : EntityGoalFactory<EntityGoalPanic> {
    override fun create(apiGoal: EntityGoalPanic, entity: PathfinderMob): Goal {
        return PanicGoal(
            entity,
            apiGoal.speed
        )
    }
}

object RandomLookAroundGoalFactory : EntityGoalFactory<EntityGoalRandomLookAround> {
    override fun create(apiGoal: EntityGoalRandomLookAround, entity: PathfinderMob): Goal {
        return RandomLookAroundGoal(
            entity
        )
    }
}

object RandomStrollGoalFactory : EntityGoalFactory<EntityGoalRandomStroll> {
    override fun create(apiGoal: EntityGoalRandomStroll, entity: PathfinderMob): Goal {
        return RandomStrollGoal(
            entity,
            apiGoal.speed,
            apiGoal.interval,
            apiGoal.canDespawn
        )
    }
}

object RandomSwimmingGoalFactory : EntityGoalFactory<EntityGoalRandomSwimming> {
    override fun create(apiGoal: EntityGoalRandomSwimming, entity: PathfinderMob): Goal {
        return RandomSwimmingGoal(
            entity,
            apiGoal.speed,
            apiGoal.interval
        )
    }
}

object RangedAttackGoalFactory : EntityGoalFactory<EntityGoalRangedAttack> {
    override fun create(apiGoal: EntityGoalRangedAttack, entity: PathfinderMob): Goal? {
        return RangedAttackGoal(
            entity as? RangedAttackMob ?: return null,
            apiGoal.mobSpeed,
            apiGoal.minInterval,
            apiGoal.maxInterval,
            apiGoal.maxRange.toFloat()
        )
    }
}

object RangedBowAttackGoalFactory : EntityGoalFactory<EntityGoalRangedBowAttack> {
    override fun create(apiGoal: EntityGoalRangedBowAttack, entity: PathfinderMob): Goal? {
        return RangedBowAttackGoal(
            entity.tryCastForThis() ?: return null,
            apiGoal.speed,
            apiGoal.attackInterval,
            apiGoal.range.toFloat()
        )
    }

    private fun <T> PathfinderMob.tryCastForThis(): T? where T : Monster, T : RangedAttackMob = this.tryCast()
}

object RangedCrossbowAttackGoalFactory : EntityGoalFactory<EntityGoalRangedCrossbowAttack> {
    override fun create(apiGoal: EntityGoalRangedCrossbowAttack, entity: PathfinderMob): Goal? {
        return RangedCrossbowAttackGoal(
            entity.tryCastForThis() ?: return null,
            apiGoal.speed,
            apiGoal.range.toFloat()
        )
    }

    private fun <T> PathfinderMob.tryCastForThis(): T? where T : Monster, T : RangedAttackMob, T : CrossbowAttackMob = this.tryCast()
}

object RestrictSunGoalFactory : EntityGoalFactory<EntityGoalRestrictSun> {
    override fun create(apiGoal: EntityGoalRestrictSun, entity: PathfinderMob): Goal {
        return RestrictSunGoal(
            entity
        )
    }
}

object StrollThroughVillageGoalFactory : EntityGoalFactory<EntityGoalStrollThroughVillage> {
    override fun create(apiGoal: EntityGoalStrollThroughVillage, entity: PathfinderMob): Goal {
        return StrollThroughVillageGoal(
            entity,
            apiGoal.searchRange
        )
    }
}

object TemptGoalFactory : EntityGoalFactory<EntityGoalTempt> {
    override fun create(apiGoal: EntityGoalTempt, entity: PathfinderMob): Goal {
        return TemptGoal(
            entity,
            apiGoal.speed,
            Ingredient.of(*apiGoal.items.map { CraftItemStack.asNMSCopy(it) }.toTypedArray()),
            apiGoal.canBeScared
        )
    }
}

object TryFindWaterGoalFactory : EntityGoalFactory<EntityGoalTryFindWater> {
    override fun create(apiGoal: EntityGoalTryFindWater, entity: PathfinderMob): Goal {
        return TryFindWaterGoal(
            entity
        )
    }
}

object UseItemGoalFactory : EntityGoalFactory<EntityGoalUseItem> {
    override fun create(apiGoal: EntityGoalUseItem, entity: PathfinderMob): Goal {
        return UseItemGoal(
            entity,
            CraftItemStack.asNMSCopy(apiGoal.item),
            SoundEvent(CraftNamespacedKey.toMinecraft(apiGoal.sound.key)),
        ) {
            apiGoal.condition.test(it.toBukkitEntity())
        }
    }
}

object WaterAvoidingRandomFlyingGoalFactory : EntityGoalFactory<EntityGoalWaterAvoidingRandomFlying> {
    override fun create(apiGoal: EntityGoalWaterAvoidingRandomFlying, entity: PathfinderMob): Goal {
        return WaterAvoidingRandomFlyingGoal(
            entity,
            apiGoal.speed
        )
    }
}

object WaterAvoidingRandomStrollGoalFactory : EntityGoalFactory<EntityGoalWaterAvoidingRandomStroll> {
    override fun create(apiGoal: EntityGoalWaterAvoidingRandomStroll, entity: PathfinderMob): Goal {
        return WaterAvoidingRandomStrollGoal(
            entity,
            apiGoal.speed,
            apiGoal.chance.toFloat() / 100
        )
    }
}

