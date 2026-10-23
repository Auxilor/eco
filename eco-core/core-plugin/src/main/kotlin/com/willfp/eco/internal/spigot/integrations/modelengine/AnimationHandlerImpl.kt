package com.willfp.eco.internal.spigot.integrations.modelengine

import com.ticxo.modelengine.api.animation.handler.AnimationHandler
import com.willfp.eco.core.integrations.modelengine.BridgedActiveModel
import com.willfp.eco.core.integrations.modelengine.BridgedAnimation
import com.willfp.eco.core.integrations.modelengine.BridgedAnimationHandler

class AnimationHandlerImpl(
    private val handle: AnimationHandler,
    private val model: BridgedActiveModel
) : BridgedAnimationHandler {
    override fun getModel(): BridgedActiveModel {
        return model
    }

    override fun getAnimation(name: String): BridgedAnimation? {
        return handle.getAnimation(name)?.let { AnimationImpl(it) }
    }

    override fun playAnimation(animation: BridgedAnimation, force: Boolean) {
        if (animation !is AnimationImpl) {
            return
        }

        handle.playAnimation(animation.handle, force)
    }

    override fun isPlayingAnimation(name: String): Boolean {
        return handle.isPlayingAnimation(name)
    }

    override fun stopAnimation(name: String) {
        handle.stopAnimation(name)
    }
}
