package com.willfp.eco.internal.spigot.proxy.common.ai.entity

import com.willfp.eco.core.entities.TestableEntity
import com.willfp.eco.core.entities.ai.entity.EntityGoalInteract
import com.willfp.eco.internal.spigot.proxy.common.ai.EntityGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.toBukkitEntity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.InteractGoal
import net.minecraft.world.entity.player.Player

object InteractGoalFactory : EntityGoalFactory<EntityGoalInteract> {
    override fun create(apiGoal: EntityGoalInteract, entity: PathfinderMob): Goal {
        return EnhancedInteractGoal(
            entity,
            apiGoal.target,
            apiGoal.range.toFloat(),
            apiGoal.chance.toFloat(),
        )
    }

    override fun isGoalOfType(goal: Goal) = goal is InteractGoal
}

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
private class EnhancedInteractGoal(
    mob: PathfinderMob,
    private val target: TestableEntity,
    range: Float,
    chance: Float
) : InteractGoal(
    mob,
    LivingEntity::class.java,
    range,
    chance
) {
    override fun canUse(): Boolean {
        return if (mob.random.nextFloat() >= probability) {
            false
        } else {
            if (mob.target != null) {
                lookAt = mob.target
            }
            val lookAt = if (lookAtType == Player::class.java) {
                mob.level.getNearestPlayer(lookAtContext, mob, mob.x, mob.eyeY, mob.z)
            } else {
                mob.level.getNearestEntity(
                    mob.level.getEntitiesOfClass(
                        lookAtType, mob.boundingBox.inflate(
                            lookDistance.toDouble(), 3.0, lookDistance.toDouble()
                        )
                    ) { target.matches(it.toBukkitEntity()) }, // Change this line to check with TestableEntity.
                    lookAtContext, mob, mob.x, mob.eyeY, mob.z
                )
            }
            lookAt != null
        }
    }
}
