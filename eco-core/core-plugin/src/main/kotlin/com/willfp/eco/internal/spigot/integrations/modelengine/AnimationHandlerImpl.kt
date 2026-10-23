package com.willfp.eco.internal.spigot.integrations.modelengine

import com.ticxo.modelengine.api.animation.BlueprintAnimation
import com.ticxo.modelengine.api.animation.handler.AnimationHandler
import com.ticxo.modelengine.api.animation.property.SimpleProperty
import com.willfp.eco.core.integrations.modelengine.BridgedActiveModel
import com.willfp.eco.core.integrations.modelengine.BridgedAnimation
import com.willfp.eco.core.integrations.modelengine.BridgedAnimationHandler

class AnimationHandlerImpl(
    private val handle: AnimationHandler,
    private val model: BridgedActiveModel
) : BridgedAnimationHandler {
    override fun getModel(): BridgedActiveModel = model

    override fun getAnimation(name: String): BridgedAnimation? {
        val activeModel = handle.activeModel
        val property = handle.getAnimation(name)
            ?: activeModel.blueprint.animations[name]?.let { SimpleProperty(activeModel, it) }
            ?: return null

        return AnimationImpl(property)
    }

    override fun playAnimation(animation: BridgedAnimation, force: Boolean) {
        playAnimation(animation, force, false)
    }

    override fun playAnimation(animation: BridgedAnimation, force: Boolean, loop: Boolean) {
        if (animation !is AnimationImpl) {
            return
        }

        if (loop) {
            animation.handle.forceLoopMode = BlueprintAnimation.LoopMode.LOOP
        }
        handle.playAnimation(animation.handle, force)
    }

    override fun playAnimation(
        name: String,
        lerpIn: Double,
        lerpOut: Double,
        speed: Double,
        force: Boolean,
        loop: Boolean
    ): Boolean {
        val property = handle.playAnimation(name, lerpIn, lerpOut, speed, force)
            ?: return handle.isPlayingAnimation(name)

        if (loop) {
            property.forceLoopMode = BlueprintAnimation.LoopMode.LOOP
        }
        return true
    }

    override fun isPlayingAnimation(name: String): Boolean = handle.isPlayingAnimation(name)

    override fun stopAnimation(name: String) {
        handle.stopAnimation(name)
    }

    override fun forceStopAnimation(name: String) {
        handle.forceStopAnimation(name)
    }
}
