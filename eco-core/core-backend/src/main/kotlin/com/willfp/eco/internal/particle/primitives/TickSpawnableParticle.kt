package com.willfp.eco.internal.particle.primitives

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.particle.ParticleAudience
import com.willfp.eco.core.particle.SpawnableParticle
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.internal.particle.BukkitTaskCancellable
import com.willfp.eco.internal.particle.EvaluationScope
import com.willfp.eco.internal.particle.NoOpCancellable
import com.willfp.eco.internal.particle.ParticleExpression
import com.willfp.eco.internal.particle.ParticleVars
import com.willfp.eco.internal.particle.ScopedSpawn
import com.willfp.eco.internal.particle.sanitiseDouble
import com.willfp.eco.internal.particle.spawnWith
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.Cancellable
import org.bukkit.scheduler.BukkitTask
import java.util.concurrent.atomic.AtomicReference

/**
 * Type `tick` — schedules a child to spawn every [intervalExpr] ticks for
 * [durationExpr] ticks total. `interval` and `duration` are evaluated ONCE at start.
 */
internal class TickSpawnableParticle(
    private val plugin: EcoPlugin,
    private val intervalExpr: ParticleExpression,
    private val durationExpr: ParticleExpression,
    private val offsetXExpr: ParticleExpression,
    private val offsetYExpr: ParticleExpression,
    private val offsetZExpr: ParticleExpression,
    private val configuredAudience: ParticleAudience,
    private val vars: ParticleVars,
    private val startVarNames: List<String>,
    private val tickVarNames: List<String>,
    private val child: SpawnableParticle
) : SpawnableParticle, ScopedSpawn {

    override fun spawn(
        location: Location,
        context: PlaceholderContext,
        audience: ParticleAudience
    ): Cancellable = spawnScoped(location, context, audience, EvaluationScope.empty(context))

    override fun spawnScoped(
        location: Location,
        context: PlaceholderContext,
        audience: ParticleAudience,
        outerScope: EvaluationScope
    ): Cancellable {
        val effective: ParticleAudience =
            if (audience === ParticleAudience.DEFAULT) configuredAudience else audience

        val startValues = DoubleArray(startVarNames.size) { i -> outerScope.lookup(startVarNames[i]) }
        val interval = sanitiseDouble(intervalExpr.evaluate(startValues)).toLong().coerceAtLeast(1L)
        val duration = sanitiseDouble(durationExpr.evaluate(startValues)).toLong()

        if (!Bukkit.isPrimaryThread()) {
            val handleRef = AtomicReference<BukkitTaskCancellable?>(null)
            plugin.scheduler.run {
                handleRef.set(start(location, context, effective, interval, duration, outerScope) as BukkitTaskCancellable)
            }
            return object : Cancellable {
                @Volatile private var cancelled = false
                override fun isCancelled(): Boolean = cancelled || (handleRef.get()?.isCancelled == true)
                override fun setCancelled(cancel: Boolean) {
                    if (cancel) {
                        cancelled = true
                        handleRef.get()?.isCancelled = true
                    }
                }
            }
        }
        return start(location, context, effective, interval, duration, outerScope)
    }

    private fun start(
        location: Location,
        context: PlaceholderContext,
        effective: ParticleAudience,
        interval: Long,
        duration: Long,
        outerScope: EvaluationScope
    ): Cancellable {
        var tick = 0L
        val taskRef = AtomicReference<BukkitTask?>(null)

        val task = plugin.scheduler.runTimer(0L, interval) {
            val tickReserved = outerScope.withReserved(mapOf("t" to tick.toDouble()))
            val tickScope = vars.applyTo(tickReserved)
            val values = DoubleArray(tickVarNames.size) { i -> tickScope.lookup(tickVarNames[i]) }

            val ox = sanitiseDouble(offsetXExpr.evaluate(values))
            val oy = sanitiseDouble(offsetYExpr.evaluate(values))
            val oz = sanitiseDouble(offsetZExpr.evaluate(values))

            child.spawnWith(location.clone().add(ox, oy, oz), context, effective, tickScope)

            tick++
            val current = taskRef.get()
            if (duration in 0..tick && current != null) {
                current.cancel()
            }
        }
        taskRef.set(task)
        return BukkitTaskCancellable(task)
    }
}