package com.willfp.eco.internal.particle

import org.bukkit.event.Cancellable
import org.bukkit.scheduler.BukkitTask

/**
 * Adapter wrapping a [BukkitTask] in a [Cancellable] handle.
 *
 * Eco does not (yet) have a dedicated scheduling-cancellation type, so we
 * reuse Bukkit's event [Cancellable] interface. The semantic mismatch
 * (`setCancelled(false)` after true is a no-op) is documented and accepted.
 */
internal class BukkitTaskCancellable(private val task: BukkitTask) : Cancellable {
    @Volatile private var cancelled = false

    override fun isCancelled(): Boolean = cancelled || task.isCancelled

    override fun setCancelled(cancel: Boolean) {
        if (cancel && !cancelled) {
            task.cancel()
            cancelled = true
        }
    }
}