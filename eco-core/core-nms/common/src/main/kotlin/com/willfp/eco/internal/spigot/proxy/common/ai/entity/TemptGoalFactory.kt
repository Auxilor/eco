package com.willfp.eco.internal.spigot.proxy.common.ai.entity

import com.willfp.eco.core.entities.ai.entity.EntityGoalTempt
import com.willfp.eco.core.items.TestableItem
import com.willfp.eco.core.recipe.parts.GroupedTestableItems
import com.willfp.eco.internal.spigot.proxy.common.ai.EntityGoalFactory
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.TemptGoal
import net.minecraft.world.entity.ai.targeting.TargetingConditions
import java.util.EnumSet
import kotlin.math.abs

object TemptGoalFactory : EntityGoalFactory<EntityGoalTempt> {
    override fun create(apiGoal: EntityGoalTempt, entity: PathfinderMob): Goal {
        return EnhancedTemptGoal(
            entity,
            apiGoal.speed,
            GroupedTestableItems(apiGoal.items),
            apiGoal.canBeScared
        )
    }

    override fun isGoalOfType(goal: Goal) = goal is TemptGoal || goal is EnhancedTemptGoal
}

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class EnhancedTemptGoal(
    private val mob: PathfinderMob,
    private val speedModifier: Double,
    private val item: TestableItem,
    private val canScare: Boolean
) : Goal() {
    private val targetingConditions: TargetingConditions
    private var px = 0.0
    private var py = 0.0
    private var pz = 0.0
    private var pRotX = 0.0
    private var pRotY = 0.0
    private var player: LivingEntity? = null
    private var calmDown = 0
    private var isRunning = false

    init {
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK))
        targetingConditions = TEMP_TARGETING.copy().selector { entity, level -> shouldFollow(entity) }
    }

    override fun canUse(): Boolean {
        return if (calmDown > 0) {
            --calmDown
            false
        } else {
            player = mob.level().getNearestPlayer(mob, 10.0)
            player != null
        }
    }

    private fun shouldFollow(entity: LivingEntity): Boolean {
        return item.matches(entity.mainHandItem.asBukkitCopy()) || item.matches(entity.offhandItem.asBukkitCopy())
    }

    override fun canContinueToUse(): Boolean {
        if (canScare()) {
            val p = player ?: return false

            if (mob.distanceToSqr(p) < 36.0) {
                if (p.distanceToSqr(px, py, pz) > 0.010000000000000002) {
                    return false
                }
                if (abs(p.xRot.toDouble() - pRotX) > 5.0 || abs(
                        p.yRot.toDouble() - pRotY
                    ) > 5.0
                ) {
                    return false
                }
            } else {
                px = p.x
                py = p.y
                pz = p.z
            }
            pRotX = p.xRot.toDouble()
            pRotY = p.yRot.toDouble()
        }
        return canUse()
    }

    private fun canScare(): Boolean {
        return canScare
    }

    override fun start() {
        val p = player ?: return

        px = p.x
        py = p.y
        pz = p.z
        isRunning = true
    }

    override fun stop() {
        player = null
        mob.navigation.stop()
        calmDown = 100
        isRunning = false
    }

    override fun tick() {
        player ?: return

        mob.lookControl.setLookAt(player!!, (mob.maxHeadYRot + 20).toFloat(), mob.maxHeadXRot.toFloat())
        if (mob.distanceToSqr(player as Entity) < 6.25) {
            mob.navigation.stop()
        } else {
            mob.navigation.moveTo(player as Entity, speedModifier)
        }
    }

    companion object {
        private val TEMP_TARGETING = TargetingConditions.forNonCombat().range(10.0).ignoreLineOfSight()
    }
}
