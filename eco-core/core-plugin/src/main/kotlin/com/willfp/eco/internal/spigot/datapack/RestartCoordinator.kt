package com.willfp.eco.internal.spigot.datapack

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.logging.Logger

/**
 * Batches restart signalling.
 *
 * Eco never restarts the server. It signals, and the admin or the consuming plugin decides.
 *
 * Signalling is batched because the alternative is noise: three plugins each adding a biome should
 * produce one prompt, not three.
 */
class RestartCoordinator(
    private val logger: Logger
) {
    private val pending = ConcurrentHashMap.newKeySet<String>()
    private val announced = AtomicBoolean(false)

    /**
     * Whether any plugin has bootstrap-only content that is not yet live.
     */
    val restartPending: Boolean
        get() = pending.isNotEmpty()

    /**
     * The plugins waiting on a restart.
     */
    fun pendingPlugins(): Set<String> = pending.toSet()

    /**
     * Whether a specific plugin is waiting on a restart.
     */
    fun isPending(pluginId: String) = pluginId in pending

    /**
     * Record that a plugin wrote bootstrap-only content.
     */
    fun markPending(pluginId: String) {
        if (pending.add(pluginId)) {
            announced.set(false)
        }
    }

    /**
     * Log the batched warning, once, for everything pending so far.
     */
    fun announce() {
        if (pending.isEmpty() || !announced.compareAndSet(false, true)) {
            return
        }

        val plugins = pending.sorted().joinToString(", ")

        logger.warning("Datapack content was written by: $plugins")
        logger.warning(
            "This content only registers at server boot, so a restart is required before it works."
        )
        logger.warning(
            "Do not create new worlds before restarting: they would be generated against the old registries."
        )
    }
}
