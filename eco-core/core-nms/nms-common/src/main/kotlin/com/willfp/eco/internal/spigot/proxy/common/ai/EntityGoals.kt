package com.willfp.eco.internal.spigot.proxy.common.ai

import com.willfp.eco.core.entities.ai.CustomGoal
import com.willfp.eco.core.entities.ai.EntityGoal
import com.willfp.eco.core.entities.ai.entity.EntityGoalAvoidEntity
import com.willfp.eco.core.entities.ai.entity.EntityGoalBreakDoors
import com.willfp.eco.core.entities.ai.entity.EntityGoalBreatheAir
import com.willfp.eco.core.entities.ai.entity.EntityGoalBreed
import com.willfp.eco.core.entities.ai.entity.EntityGoalCatLieOnBed
import com.willfp.eco.core.entities.ai.entity.EntityGoalCatSitOnBed
import com.willfp.eco.core.entities.ai.entity.EntityGoalEatGrass
import com.willfp.eco.core.entities.ai.entity.EntityGoalFleeSun
import com.willfp.eco.core.entities.ai.entity.EntityGoalFloat
import com.willfp.eco.core.entities.ai.entity.EntityGoalFollowBoats
import com.willfp.eco.core.entities.ai.entity.EntityGoalFollowMobs
import com.willfp.eco.core.entities.ai.entity.EntityGoalIllusionerBlindnessSpell
import com.willfp.eco.core.entities.ai.entity.EntityGoalIllusionerMirrorSpell
import com.willfp.eco.core.entities.ai.entity.EntityGoalInteract
import com.willfp.eco.core.entities.ai.entity.EntityGoalLeapAtTarget
import com.willfp.eco.core.entities.ai.entity.EntityGoalLookAtPlayer
import com.willfp.eco.core.entities.ai.entity.EntityGoalMeleeAttack
import com.willfp.eco.core.entities.ai.entity.EntityGoalMoveBackToVillage
import com.willfp.eco.core.entities.ai.entity.EntityGoalMoveThroughVillage
import com.willfp.eco.core.entities.ai.entity.EntityGoalMoveTowardsRestriction
import com.willfp.eco.core.entities.ai.entity.EntityGoalMoveTowardsTarget
import com.willfp.eco.core.entities.ai.entity.EntityGoalOcelotAttack
import com.willfp.eco.core.entities.ai.entity.EntityGoalOpenDoors
import com.willfp.eco.core.entities.ai.entity.EntityGoalPanic
import com.willfp.eco.core.entities.ai.entity.EntityGoalRandomLookAround
import com.willfp.eco.core.entities.ai.entity.EntityGoalRandomStroll
import com.willfp.eco.core.entities.ai.entity.EntityGoalRandomSwimming
import com.willfp.eco.core.entities.ai.entity.EntityGoalRangedAttack
import com.willfp.eco.core.entities.ai.entity.EntityGoalRangedBowAttack
import com.willfp.eco.core.entities.ai.entity.EntityGoalRangedCrossbowAttack
import com.willfp.eco.core.entities.ai.entity.EntityGoalRestrictSun
import com.willfp.eco.core.entities.ai.entity.EntityGoalStrollThroughVillage
import com.willfp.eco.core.entities.ai.entity.EntityGoalTempt
import com.willfp.eco.core.entities.ai.entity.EntityGoalTryFindWater
import com.willfp.eco.core.entities.ai.entity.EntityGoalUseItem
import com.willfp.eco.core.entities.ai.entity.EntityGoalWaterAvoidingRandomFlying
import com.willfp.eco.core.entities.ai.entity.EntityGoalWaterAvoidingRandomStroll
import com.willfp.eco.core.entities.ai.entity.EntityGoalWolfBeg
import com.willfp.eco.internal.spigot.proxy.common.ai.entity.AvoidEntityGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.entity.BreakDoorsGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.entity.BreatheAirGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.entity.BreedGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.entity.CatLieOnBedGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.entity.CatSitOnBedGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.entity.EatGrassGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.entity.FleeSunGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.entity.FloatGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.entity.FollowBoatsGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.entity.FollowMobsGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.entity.IllusionerBlindnessSpellGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.entity.IllusionerMirrorSpellGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.entity.InteractGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.entity.LeapAtTargetGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.entity.LookAtPlayerGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.entity.MeleeAttackGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.entity.MoveBackToVillageGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.entity.MoveThroughVillageGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.entity.MoveTowardsRestrictionGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.entity.MoveTowardsTargetGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.entity.OcelotAttackGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.entity.OpenDoorsGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.entity.PanicGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.entity.RandomLookAroundGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.entity.RandomStrollGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.entity.RandomSwimmingGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.entity.RangedAttackGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.entity.RangedBowAttackGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.entity.RangedCrossbowAttackGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.entity.RestrictSunGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.entity.StrollThroughVillageGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.entity.TemptGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.entity.TryFindWaterGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.entity.UseItemGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.entity.WaterAvoidingRandomFlyingGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.entity.WaterAvoidingRandomStrollGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.entity.WolfBegGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.getVersionSpecificEntityGoalFactory
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal

fun <T : EntityGoal<*>> T.getGoalFactory(): EntityGoalFactory<T>? {
    val versionSpecific = this.getVersionSpecificEntityGoalFactory()
    if (versionSpecific != null) {
        return versionSpecific
    }

    @Suppress("UNCHECKED_CAST")
    return when (this) {
        is EntityGoalAvoidEntity -> AvoidEntityGoalFactory
        is EntityGoalBreakDoors -> BreakDoorsGoalFactory
        is EntityGoalBreatheAir -> BreatheAirGoalFactory
        is EntityGoalEatGrass -> EatGrassGoalFactory
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
        is EntityGoalWolfBeg -> WolfBegGoalFactory
        is EntityGoalBreed -> BreedGoalFactory
        is EntityGoalCatSitOnBed -> CatSitOnBedGoalFactory
        is EntityGoalCatLieOnBed -> CatLieOnBedGoalFactory
        is EntityGoalIllusionerBlindnessSpell -> IllusionerBlindnessSpellGoalFactory
        is EntityGoalIllusionerMirrorSpell -> IllusionerMirrorSpellGoalFactory
        is CustomGoal<*> -> CustomGoalFactory
        else -> null
    } as EntityGoalFactory<T>?
}

interface EntityGoalFactory<T : EntityGoal<*>> {
    fun create(apiGoal: T, entity: PathfinderMob): Goal?
    fun isGoalOfType(goal: Goal): Boolean
}
