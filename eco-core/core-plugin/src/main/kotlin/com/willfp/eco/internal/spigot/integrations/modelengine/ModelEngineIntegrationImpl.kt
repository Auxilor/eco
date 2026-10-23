package com.willfp.eco.internal.spigot.integrations.modelengine

import com.ticxo.modelengine.api.ModelEngineAPI
import com.willfp.eco.core.integrations.modelengine.BridgedActiveModel
import com.willfp.eco.core.integrations.modelengine.BridgedModeledEntity
import com.willfp.eco.core.integrations.modelengine.ModelEngineIntegration
import org.bukkit.entity.Entity
import java.util.UUID

class ModelEngineIntegrationImpl : ModelEngineIntegration {
    override fun createActiveModel(id: String): BridgedActiveModel? {
        val blueprint = ModelEngineAPI.getBlueprint(id) ?: return null

        return ActiveModelImpl(id, ModelEngineAPI.createActiveModel(blueprint) ?: return null)
    }

    override fun createModeledEntity(entity: Entity): BridgedModeledEntity {
        return ModeledEntityImpl(ModelEngineAPI.getOrCreateModeledEntity(entity))
    }

    override fun getModeledEntity(uuid: UUID): BridgedModeledEntity? {
        return ModelEngineAPI.getModeledEntity(uuid)?.let { ModeledEntityImpl(it) }
    }

    override fun getPluginName(): String {
        return "ModelEngine"
    }
}
