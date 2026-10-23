package com.willfp.eco.internal.spigot.integrations.model

import com.ticxo.modelengine.api.ModelEngineAPI
import com.ticxo.modelengine.api.animation.BlueprintAnimation
import com.ticxo.modelengine.api.animation.ModelState as ModelEngineState
import com.ticxo.modelengine.api.animation.handler.AnimationHandler
import com.willfp.eco.core.integrations.model.ModelAnimation
import com.willfp.eco.core.integrations.model.ModelIntegration
import com.willfp.eco.core.integrations.model.ModelState
import org.bukkit.entity.Entity

class ModelIntegrationModelEngine : ModelIntegration {
    override fun getId(): String = "model-engine"

    override fun getPluginName(): String = "ModelEngine"

    override fun modelExists(modelId: String): Boolean = ModelEngineAPI.getBlueprint(modelId) != null

    override fun attachModel(entity: Entity, modelId: String): Boolean {
        if (hasModel(entity, modelId)) {
            return true
        }

        val blueprint = ModelEngineAPI.getBlueprint(modelId) ?: return false
        ModelEngineAPI.getOrCreateModeledEntity(entity).addModel(ModelEngineAPI.createActiveModel(blueprint), true)
        return true
    }

    override fun hasModel(entity: Entity, modelId: String?): Boolean = handlers(entity, modelId).isNotEmpty()

    override fun setBaseEntityVisible(entity: Entity, visible: Boolean) {
        ModelEngineAPI.getModeledEntity(entity.uniqueId)?.isBaseEntityVisible = visible
    }

    override fun playAnimation(entity: Entity, modelId: String?, name: String, animation: ModelAnimation): Boolean =
        handlers(entity, modelId).filter { play(it, name, animation) }.isNotEmpty()

    override fun stopAnimation(entity: Entity, modelId: String?, name: String, fade: Boolean): Boolean {
        val handlers = handlers(entity, modelId).filter { it.isPlayingAnimation(name) }

        for (handler in handlers) {
            if (fade) {
                handler.stopAnimation(name)
            } else {
                handler.forceStopAnimation(name)
            }
        }

        return handlers.isNotEmpty()
    }

    override fun isPlayingAnimation(entity: Entity, modelId: String?, name: String): Boolean =
        handlers(entity, modelId).any { it.isPlayingAnimation(name) }

    override fun setStateAnimation(entity: Entity, modelId: String?, state: ModelState, name: String) {
        for (handler in handlers(entity, modelId)) {
            handler.setDefaultProperty(
                AnimationHandler.DefaultProperty(ModelEngineState.valueOf(state.name), name, 0.0, 0.0, 1.0)
            )
        }
    }

    private fun play(handler: AnimationHandler, name: String, animation: ModelAnimation): Boolean {
        val property = handler.playAnimation(name, animation.lerpIn, animation.lerpOut, animation.speed, animation.force)
            ?: return handler.isPlayingAnimation(name)

        when (animation.loopMode) {
            ModelAnimation.LoopMode.LOOP -> property.forceLoopMode = BlueprintAnimation.LoopMode.LOOP
            ModelAnimation.LoopMode.ONCE -> property.forceLoopMode = BlueprintAnimation.LoopMode.ONCE
            ModelAnimation.LoopMode.HOLD -> property.forceLoopMode = BlueprintAnimation.LoopMode.HOLD
            ModelAnimation.LoopMode.MODEL -> Unit
        }
        return true
    }

    private fun handlers(entity: Entity, modelId: String?): List<AnimationHandler> =
        ModelEngineAPI.getModeledEntity(entity.uniqueId)?.models.orEmpty()
            .filterKeys { modelId == null || it.equals(modelId, ignoreCase = true) }
            .values
            .map { it.animationHandler }
}
