package com.willfp.eco.internal.particle.primitives

import com.willfp.eco.core.particle.ParticleAudience
import com.willfp.eco.core.particle.SpawnableParticle
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.internal.particle.EvaluationScope
import com.willfp.eco.internal.particle.NoOpCancellable
import com.willfp.eco.internal.particle.ParticleExpression
import com.willfp.eco.internal.particle.ParticleVars
import com.willfp.eco.internal.particle.sanitiseCount
import com.willfp.eco.internal.particle.sanitiseDouble
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.event.Cancellable
import org.bukkit.inventory.ItemStack

/**
 * Type `simple` — wraps a Bukkit [Particle] and makes the actual spawn call.
 */
internal class SimpleSpawnableParticle(
    private val particle: Particle,
    private val countExpr: ParticleExpression,
    private val spreadXExpr: ParticleExpression,
    private val spreadYExpr: ParticleExpression,
    private val spreadZExpr: ParticleExpression,
    private val speedExpr: ParticleExpression,
    private val force: Boolean,
    private val dustSizeExpr: ParticleExpression,
    private val dustColor: Color,
    private val dustTransitionTo: Color?,
    private val block: Material?,
    private val item: ItemStack?,
    private val configuredAudience: ParticleAudience,
    private val vars: ParticleVars,
    private val fieldVarNames: List<String>
) : SpawnableParticle {

    override fun spawn(
        location: Location,
        context: PlaceholderContext,
        audience: ParticleAudience
    ): Cancellable {
        location.world ?: return NoOpCancellable

        val effective: ParticleAudience =
            if (audience === ParticleAudience.DEFAULT) configuredAudience else audience
        val resolved: ParticleAudience =
            if (effective === ParticleAudience.DEFAULT) ParticleAudience.WORLD else effective

        val baseScope = EvaluationScope.empty(context)
        val scope = vars.applyTo(baseScope)
        val values = DoubleArray(fieldVarNames.size) { i -> scope.lookup(fieldVarNames[i]) }

        val count = sanitiseCount(countExpr.evaluate(values))
        val spreadX = sanitiseDouble(spreadXExpr.evaluate(values))
        val spreadY = sanitiseDouble(spreadYExpr.evaluate(values))
        val spreadZ = sanitiseDouble(spreadZExpr.evaluate(values))
        val speed = sanitiseDouble(speedExpr.evaluate(values))

        if (count <= 0) return NoOpCancellable

        val data: Any? = resolveData(values)

        if (!Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTask(plugin(), Runnable {
                emit(resolved, location, count, spreadX, spreadY, spreadZ, speed, data, context)
            })
            return NoOpCancellable
        }

        emit(resolved, location, count, spreadX, spreadY, spreadZ, speed, data, context)
        return NoOpCancellable
    }

    private fun resolveData(values: DoubleArray): Any? {
        return when (particle) {
            Particle.DUST -> {
                val size = sanitiseDouble(dustSizeExpr.evaluate(values)).toFloat().coerceAtLeast(0.01f)
                Particle.DustOptions(dustColor, size)
            }
            Particle.DUST_COLOR_TRANSITION -> {
                val size = sanitiseDouble(dustSizeExpr.evaluate(values)).toFloat().coerceAtLeast(0.01f)
                val to = dustTransitionTo ?: dustColor
                Particle.DustTransition(dustColor, to, size)
            }
            Particle.BLOCK, Particle.BLOCK_MARKER, Particle.FALLING_DUST -> {
                block?.createBlockData()
            }
            Particle.ITEM -> item
            else -> null
        }
    }

    private fun emit(
        audience: ParticleAudience,
        location: Location,
        count: Int,
        sx: Double,
        sy: Double,
        sz: Double,
        speed: Double,
        data: Any?,
        context: PlaceholderContext
    ) {
        val world = location.world ?: return
        if (audience === ParticleAudience.WORLD) {
            world.spawnParticle(particle, location, count, sx, sy, sz, speed, data, force)
            return
        }
        for (viewer in audience.getViewers(location, context)) {
            viewer.spawnParticle(particle, location, count, sx, sy, sz, speed, data)
        }
    }

    companion object {
        @Volatile private var pluginRef: org.bukkit.plugin.Plugin? = null

        internal fun setPlugin(plugin: org.bukkit.plugin.Plugin) { pluginRef = plugin }

        internal fun plugin(): org.bukkit.plugin.Plugin =
            pluginRef ?: error("SimpleSpawnableParticle.setPlugin not called yet")
    }

    private fun plugin() = SimpleSpawnableParticle.plugin()
}