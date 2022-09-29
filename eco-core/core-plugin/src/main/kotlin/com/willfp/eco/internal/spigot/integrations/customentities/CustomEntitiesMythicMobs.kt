package com.willfp.eco.internal.spigot.integrations.customentities

/*
class CustomEntitiesMythicMobs : CustomEntitiesIntegration {
    override fun registerAllEntities() {
        val mobManager = MythicBukkit.inst().mobManager
        val api = MythicBukkit.inst().apiHelper

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

 */