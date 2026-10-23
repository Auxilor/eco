package com.willfp.eco.internal.spigot.integrations.modelengine

import com.ticxo.modelengine.api.ModelEngineAPI
import com.willfp.eco.core.integrations.modelengine.BridgedActiveModel
import com.willfp.eco.core.integrations.modelengine.BridgedModeledEntity
import com.willfp.eco.core.integrations.modelengine.ModelEngineIntegration
import org.bukkit.entity.Entity
import java.util.UUID

class ModelEngineIntegrationImpl : ModelEngineIntegration {
    override fun createActiveModel(id: String): BridgedActiveModel? =
        ModelEngineAPI.getBlueprint(id)
            ?.let { ModelEngineAPI.createActiveModel(it) }
            ?.let { ActiveModelImpl(id, it) }

    override fun createModeledEntity(entity: Entity): BridgedModeledEntity =
        ModeledEntityImpl(ModelEngineAPI.getOrCreateModeledEntity(entity))

    override fun getModeledEntity(uuid: UUID): BridgedModeledEntity? =
        ModelEngineAPI.getModeledEntity(uuid)?.let { ModeledEntityImpl(it) }

    override fun getPluginName(): String = "ModelEngine"
}
