package com.willfp.eco.internal.spigot.integrations.customentities

import com.willfp.eco.core.entities.CustomEntity
import com.willfp.eco.core.integrations.customentities.CustomEntitiesWrapper
import io.lumine.xikage.mythicmobs.MythicMobs
import org.bukkit.NamespacedKey

class CustomEntitiesMythicMobs : CustomEntitiesWrapper {
    override fun registerAllEntities() {
        val mobManager = MythicMobs.inst().mobManager
        val api = MythicMobs.inst().apiHelper

        for (id in mobManager.mobNames) {
            val key = NamespacedKey.fromString("mythicmobs:${id.lowercase()}")
            key ?: continue
            CustomEntity(
                key,
                {
                    val entityId = api.getMythicMobInstance(it)?.type?.entityType ?: return@CustomEntity false
                    entityId.equals(id, ignoreCase = true)
                },
                {
                    api.spawnMythicMob(id, it)
                }
            ).register()
        }
    }

    override fun getPluginName(): String {
        return "MythicMobs"
    }
}