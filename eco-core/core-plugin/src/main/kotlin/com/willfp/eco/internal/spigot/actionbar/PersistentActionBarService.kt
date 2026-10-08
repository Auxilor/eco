package com.willfp.eco.internal.spigot.actionbar

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.actionbar.ActionBarProvider
import com.willfp.eco.core.actionbar.PersistentActionBar
import com.willfp.eco.util.asAudience
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import java.util.logging.Level
import java.util.logging.Logger
import net.kyori.adventure.text.Component
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player

/**
 * Picks and sends each player's highest priority persistent action bar, yielding to any other
 * action bar message for a few seconds.
 */
class PersistentActionBarService(
    private val logger: Logger,
    private val clock: () -> Long = System::currentTimeMillis,
    private val sender: (Player, Component) -> Unit = { player, component ->
        player.asAudience().sendActionBar(component)
    }
) {
    private val bars = ConcurrentHashMap<NamespacedKey, EcoPersistentActionBar>()

    private val failedBars: MutableSet<NamespacedKey> = ConcurrentHashMap.newKeySet()

    private val states = ConcurrentHashMap<UUID, PersistentActionBarState>()

    private val registrations = AtomicLong()

    @Volatile
    private var orderedBars = emptyList<EcoPersistentActionBar>()

    fun register(
        plugin: EcoPlugin,
        id: String,
        priority: Int,
        provider: ActionBarProvider
    ): PersistentActionBar {
        val bar = EcoPersistentActionBar(
            NamespacedKey(plugin.id, id),
            plugin,
            priority,
            provider,
            registrations.incrementAndGet(),
            this
        )

        bars[bar.key] = bar
        failedBars.remove(bar.key)
        reorder()

        return bar
    }

    fun unregister(bar: EcoPersistentActionBar) {
        if (bars.remove(bar.key, bar)) {
            reorder()
        }
    }

    fun unregisterAll(plugin: EcoPlugin) {
        if (bars.values.removeIf { it.plugin == plugin }) {
            reorder()
        }
    }

    fun refresh(player: Player) {
        stateOf(player).forceRefresh = true
    }

    fun remove(player: Player) {
        states.remove(player.uniqueId)
    }

    fun onForeignActionBar(player: Player) {
        if (orderedBars.isEmpty()) {
            return
        }

        val state = stateOf(player)
        val now = clock()

        if (now - state.selfSendAt < SELF_SEND_MILLIS) {
            return
        }

        state.pausedUntil = now + PAUSE_MILLIS
        state.lastComponent = null
        state.lastBarKey = null
    }

    fun tick(player: Player) {
        val candidates = orderedBars

        if (candidates.isEmpty() && states[player.uniqueId]?.lastComponent == null) {
            return
        }

        val state = stateOf(player)
        val now = clock()

        if (now < state.pausedUntil) {
            return
        }

        for (bar in candidates) {
            val component = bar.render(player) ?: continue

            if (bar.key != state.lastBarKey || component != state.lastComponent || state.forceRefresh ||
                now - state.lastSentAt >= KEEP_ALIVE_MILLIS
            ) {
                send(player, state, bar.key, component, now)
            }

            return
        }

        val last = state.lastComponent

        if (last != null && last != Component.empty()) {
            send(player, state, null, Component.empty(), now)
        }
    }

    private fun send(
        player: Player,
        state: PersistentActionBarState,
        key: NamespacedKey?,
        component: Component,
        now: Long
    ) {
        state.selfSendAt = now
        sender(player, component)
        state.lastComponent = component
        state.lastBarKey = key
        state.lastSentAt = now
        state.forceRefresh = false
    }

    private fun EcoPersistentActionBar.render(player: Player): Component? {
        return try {
            provider.render(player)
        } catch (exception: Exception) {
            if (failedBars.add(key)) {
                logger.log(Level.WARNING, "Persistent action bar $key failed to render", exception)
            }
            null
        }
    }

    private fun stateOf(player: Player) =
        states.computeIfAbsent(player.uniqueId) { PersistentActionBarState() }

    private fun reorder() {
        orderedBars = bars.values.sortedWith(
            compareByDescending<EcoPersistentActionBar> { it.priority }.thenBy { it.registrationOrder }
        )
    }

    companion object {
        const val TICK_INTERVAL = 5L

        private const val KEEP_ALIVE_MILLIS = 2000L

        private const val PAUSE_MILLIS = 2700L

        private const val SELF_SEND_MILLIS = 50L
    }
}
