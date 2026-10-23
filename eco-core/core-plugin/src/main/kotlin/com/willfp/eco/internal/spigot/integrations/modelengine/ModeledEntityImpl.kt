package com.willfp.eco.internal.spigot.integrations.modelengine

import com.ticxo.modelengine.api.model.ModeledEntity
import com.willfp.eco.core.integrations.modelengine.BridgedActiveModel
import com.willfp.eco.core.integrations.modelengine.BridgedModeledEntity

class ModeledEntityImpl(
    private val handle: ModeledEntity
) : BridgedModeledEntity {
    override fun isBaseEntityVisible(): Boolean {
        return handle.isBaseEntityVisible
    }

    override fun setBaseEntityVisible(visible: Boolean) {
        handle.isBaseEntityVisible = visible
    }

    override fun getModels(): Map<String, BridgedActiveModel> {
        return handle.models.mapValues { ActiveModelImpl(it.key, it.value) }
    }

    override fun addModel(model: BridgedActiveModel) {
        if (model !is ActiveModelImpl) {
            return
        }

        handle.addModel(model.handle, true)
    }

    override fun removeModel(model: BridgedActiveModel) {
        handle.removeModel(model.id)
    }
}
