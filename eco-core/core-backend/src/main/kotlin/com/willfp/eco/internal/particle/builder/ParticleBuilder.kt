package com.willfp.eco.internal.particle.builder

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.particle.Particles
import com.willfp.eco.core.particle.SpawnableParticle
import com.willfp.eco.internal.particle.ParticleScope
import com.willfp.eco.internal.particle.config.ParticleConfigLoader

/**
 * Lightweight Kotlin builders for assembling [SpawnableParticle]s programmatically.
 * Internally these emit a YAML-like map and feed it through [ParticleConfigLoader],
 * giving us a single canonical execution path.
 */
object ParticleBuilder {

    fun simple(plugin: EcoPlugin, block: SimpleBuilder.() -> Unit): SpawnableParticle {
        val b = SimpleBuilder().apply(block)
        return loader().loadEntry(b.toMap(), plugin, ParticleScope())
    }

    fun tick(plugin: EcoPlugin, block: TickBuilder.() -> Unit): SpawnableParticle {
        val b = TickBuilder().apply(block)
        return loader().loadEntry(b.toMap(), plugin, ParticleScope())
    }

    fun iterate(plugin: EcoPlugin, block: IterateBuilder.() -> Unit): SpawnableParticle {
        val b = IterateBuilder().apply(block)
        return loader().loadEntry(b.toMap(), plugin, ParticleScope())
    }

    fun composite(plugin: EcoPlugin, block: CompositeBuilder.() -> Unit): SpawnableParticle {
        val b = CompositeBuilder().apply(block)
        return loader().loadEntry(b.toMap(), plugin, ParticleScope())
    }

    fun lookup(name: String): SpawnableParticle = Particles.lookup(name)

    private fun loader(): ParticleConfigLoader = ParticleConfigLoader()

    class SimpleBuilder {
        var particle: String = "flame"
        var count: String = "1"
        var spreadX: String = "0"; var spreadY: String = "0"; var spreadZ: String = "0"
        var speed: String = "0"
        var force: Boolean = false
        var audience: String? = null
        fun toMap(): Map<String, Any?> = buildMap {
            put("type", "simple"); put("particle", particle)
            put("count", count)
            put("spread_x", spreadX); put("spread_y", spreadY); put("spread_z", spreadZ)
            put("speed", speed); put("force", force)
            audience?.let { put("audience", it) }
        }
    }

    class TickBuilder {
        var interval: String = "1"; var duration: String = "-1"
        var offsetX: String = "0"; var offsetY: String = "0"; var offsetZ: String = "0"
        var audience: String? = null
        var child: Any = "flame"
        fun toMap(): Map<String, Any?> = buildMap {
            put("type", "tick")
            put("interval", interval); put("duration", duration)
            put("offset_x", offsetX); put("offset_y", offsetY); put("offset_z", offsetZ)
            audience?.let { put("audience", it) }
            put("child", child)
        }
    }

    class IterateBuilder {
        var count: String = "1"
        var offsetX: String = "0"; var offsetY: String = "0"; var offsetZ: String = "0"
        var audience: String? = null
        var child: Any = "flame"
        fun toMap(): Map<String, Any?> = buildMap {
            put("type", "iterate"); put("count", count)
            put("offset_x", offsetX); put("offset_y", offsetY); put("offset_z", offsetZ)
            audience?.let { put("audience", it) }
            put("child", child)
        }
    }

    class CompositeBuilder {
        var audience: String? = null
        val children: MutableList<Any> = mutableListOf()
        fun toMap(): Map<String, Any?> = buildMap {
            put("type", "composite")
            audience?.let { put("audience", it) }
            put("children", children.toList())
        }
    }
}