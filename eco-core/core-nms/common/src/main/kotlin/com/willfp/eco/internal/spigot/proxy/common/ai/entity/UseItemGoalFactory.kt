package com.willfp.eco.internal.spigot.proxy.common.ai.entity

import com.willfp.eco.core.entities.ai.entity.EntityGoalUseItem
import com.willfp.eco.internal.spigot.proxy.common.ai.EntityGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.asNMSStack
import com.willfp.eco.internal.spigot.proxy.common.toBukkitEntity
import java.util.Optional
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.UseItemGoal
import org.bukkit.Registry
import org.bukkit.craftbukkit.util.CraftNamespacedKey

object UseItemGoalFactory : EntityGoalFactory<EntityGoalUseItem> {
    override fun create(apiGoal: EntityGoalUseItem, entity: PathfinderMob): Goal {
        val sound = CraftNamespacedKey.toMinecraft(Registry.SOUNDS.getKey(apiGoal.sound)!!)

        return UseItemGoal(
            entity,
            apiGoal.item.asNMSStack(),
            SoundEvent(sound, Optional.empty()),
        ) {
            apiGoal.condition.test(it.toBukkitEntity())
        }
    }

    override fun isGoalOfType(goal: Goal) = goal is UseItemGoal<*>
}
