package com.willfp.eco.internal.spigot.integrations.modelengine

import com.ticxo.modelengine.api.animation.property.IAnimationProperty
import com.willfp.eco.core.integrations.modelengine.BridgedAnimation

class AnimationImpl(
    val handle: IAnimationProperty
) : BridgedAnimation {
    override fun getName(): String = handle.name
}
