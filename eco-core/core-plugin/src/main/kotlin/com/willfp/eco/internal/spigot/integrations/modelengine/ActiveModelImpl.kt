package com.willfp.eco.internal.spigot.integrations.modelengine

import com.ticxo.modelengine.api.model.ActiveModel
import com.willfp.eco.core.integrations.modelengine.BridgedActiveModel
import com.willfp.eco.core.integrations.modelengine.BridgedAnimationHandler

class ActiveModelImpl(
    private val id: String,
    val handle: ActiveModel
) : BridgedActiveModel {
    private val animationHandler = AnimationHandlerImpl(handle.animationHandler, this)

    override fun getId(): String {
        return id
    }

    override fun getAnimationHandler(): BridgedAnimationHandler {
        return animationHandler
    }
}
