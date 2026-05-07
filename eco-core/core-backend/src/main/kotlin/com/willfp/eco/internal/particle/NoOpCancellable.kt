package com.willfp.eco.internal.particle

import org.bukkit.event.Cancellable

/**
 * Always-cancelled [Cancellable]. Returned from non-animated [com.willfp.eco.core.particle.SpawnableParticle.spawn]
 * implementations so callers can treat the return uniformly.
 */
object NoOpCancellable : Cancellable {
    override fun isCancelled(): Boolean = true
    override fun setCancelled(cancel: Boolean) {
        /* no-op: nothing to cancel */
    }
}