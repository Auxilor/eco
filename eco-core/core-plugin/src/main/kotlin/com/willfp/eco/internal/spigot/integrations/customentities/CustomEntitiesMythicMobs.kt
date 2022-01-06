package com.willfp.eco.internal.spigot.integrations.customentities

import com.willfp.eco.core.entities.CustomEntity
import com.willfp.eco.core.integrations.customentities.CustomEntitiesWrapper
import com.willfp.eco.util.NamespacedKeyUtils
import io.lumine.xikage.mythicmobs.MythicMobs

class CustomEntitiesMythicMobs : CustomEntitiesWrapper {
    override fun registerAllEntities() {
        val mobManager = MythicMobs.inst().mobManager
        val api = MythicMobs.inst().apiHelper

        for (id in mobManager.mobNames) {
            val key = NamespacedKeyUtils.create("mythicmobs", id.lowercase())
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