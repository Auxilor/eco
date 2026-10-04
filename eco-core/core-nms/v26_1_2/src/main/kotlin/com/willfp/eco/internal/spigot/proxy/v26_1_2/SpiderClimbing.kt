package com.willfp.eco.internal.spigot.proxy.v26_1_2

import com.willfp.eco.internal.spigot.proxies.SpiderClimbingProxy
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation
import net.minecraft.world.entity.ai.navigation.PathNavigation
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation
import net.minecraft.world.entity.monster.spider.Spider
import org.bukkit.craftbukkit.entity.CraftLivingEntity
import org.bukkit.entity.LivingEntity
import java.lang.reflect.Field

class SpiderClimbing : SpiderClimbingProxy {
    override fun disableWallPathing(entity: LivingEntity) {
        val spider = (entity as CraftLivingEntity).handle as? Spider ?: return

        if (spider.navigation !is WallClimberNavigation) {
            return
        }

        spider.navigation.stop()
        navigationField.set(spider, GroundPathNavigation(spider, spider.level()))
    }

    override fun stopClimbing(entity: LivingEntity) {
        ((entity as CraftLivingEntity).handle as? Spider)?.isClimbing = false
    }

    companion object {
        private val navigationField: Field = Mob::class.java.declaredFields
            .first { it.type == PathNavigation::class.java }
            .apply { isAccessible = true }
    }
}
