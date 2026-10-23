package com.willfp.eco.internal.spigot.integrations.model

import com.willfp.eco.core.integrations.model.ModelAnimation
import com.willfp.eco.core.integrations.model.ModelIntegration
import com.willfp.eco.core.integrations.model.ModelState
import kr.toxicity.model.api.BetterModel
import kr.toxicity.model.api.animation.AnimationIterator
import kr.toxicity.model.api.animation.AnimationModifier
import kr.toxicity.model.api.bukkit.platform.BukkitAdapter
import kr.toxicity.model.api.tracker.EntityTracker
import org.bukkit.entity.Entity

class ModelIntegrationBetterModel : ModelIntegration {
    override fun getId(): String = "better-model"

    override fun getPluginName(): String = "BetterModel"

    override fun modelExists(modelId: String): Boolean = BetterModel.modelOrNull(modelId) != null

    override fun attachModel(entity: Entity, modelId: String): Boolean {
        val model = BetterModel.modelOrNull(modelId) ?: return false
        model.getOrCreate(BukkitAdapter.adapt(entity))
        return true
    }

    override fun hasModel(entity: Entity, modelId: String?): Boolean = trackers(entity, modelId).isNotEmpty()

    override fun playAnimation(entity: Entity, modelId: String?, name: String, animation: ModelAnimation): Boolean {
        val modifier = AnimationModifier.builder()
            .type(
                when (animation.loopMode) {
                    ModelAnimation.LoopMode.LOOP -> AnimationIterator.Type.LOOP
                    ModelAnimation.LoopMode.HOLD -> AnimationIterator.Type.HOLD_ON_LAST
                    ModelAnimation.LoopMode.ONCE, ModelAnimation.LoopMode.MODEL -> AnimationIterator.Type.PLAY_ONCE
                }
            )
            .speed(animation.speed.toFloat())
            .override(animation.force)
            .start((animation.lerpIn * TICKS_PER_SECOND).toInt())
            .end((animation.lerpOut * TICKS_PER_SECOND).toInt())
            .apply { animation.viewer?.let { player(BukkitAdapter.adapt(it)) } }
            .build()

        return trackers(entity, modelId).filter { it.animate(name, modifier) }.isNotEmpty()
    }

    override fun stopAnimation(entity: Entity, modelId: String?, name: String, fade: Boolean): Boolean =
        trackers(entity, modelId).filter { it.stopAnimation(name) }.isNotEmpty()

    override fun isPlayingAnimation(entity: Entity, modelId: String?, name: String): Boolean = false

    override fun setStateAnimation(entity: Entity, modelId: String?, state: ModelState, name: String) {
        val loop = AnimationModifier.builder().type(AnimationIterator.Type.LOOP).build()

        for (tracker in trackers(entity, modelId)) {
            tracker.replace(state.name.lowercase(), name, loop)
        }
    }

    private fun trackers(entity: Entity, modelId: String?): List<EntityTracker> =
        BetterModel.registryOrNull(entity.uniqueId)?.trackers().orEmpty()
            .filter { modelId == null || it.name().equals(modelId, ignoreCase = true) }

    private companion object {
        const val TICKS_PER_SECOND = 20
    }
}
