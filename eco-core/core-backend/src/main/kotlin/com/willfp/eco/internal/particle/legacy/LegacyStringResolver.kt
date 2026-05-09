package com.willfp.eco.internal.particle.legacy

import com.willfp.eco.core.particle.ParticleAudience
import com.willfp.eco.core.particle.Particles
import com.willfp.eco.core.particle.SpawnableParticle
import com.willfp.eco.core.particle.impl.EmptyParticle
import com.willfp.eco.internal.particle.ParticleExpression
import com.willfp.eco.internal.particle.ParticleVars
import com.willfp.eco.internal.particle.primitives.SimpleSpawnableParticle
import org.bukkit.Color
import org.bukkit.NamespacedKey
import org.bukkit.Particle

/**
 * Resolves legacy single-token particle strings:
 *   - bare vanilla name: `"flame"`, `"magic"`
 *   - `minecraft:`-prefixed: `"minecraft:flame"`
 *   - factory shorthands: `"rgb:ff00ff"`, `"hex:00ff00"`, `"color:ff8800"`
 *   - namespaced registry key: `"eco:circle"`
 */
internal object LegacyStringResolver {

    fun resolve(key: String): SpawnableParticle {
        val trimmed = key.trim()
        if (trimmed.isEmpty()) return EmptyParticle.INSTANCE

        if (trimmed.contains(':')) {
            val parts = trimmed.split(':', limit = 2)
            val ns = parts[0].lowercase()
            val rest = parts[1]

            if (ns == "rgb" || ns == "hex" || ns == "color") {
                return resolveDust(rest) ?: EmptyParticle.INSTANCE
            }

            if (ns == "minecraft") {
                return resolveVanilla(rest)
            }

            val nsKey = try { NamespacedKey(ns, rest.lowercase()) } catch (_: Throwable) { null }
            if (nsKey != null) {
                val found = Particles.find(nsKey)
                if (found != null) return found
            }
            return EmptyParticle.INSTANCE
        }

        val vanilla = resolveVanilla(trimmed)
        if (vanilla !is EmptyParticle) return vanilla

        val ecoKey = try { NamespacedKey("eco", trimmed.lowercase()) } catch (_: Throwable) { null }
        if (ecoKey != null) {
            val found = Particles.find(ecoKey)
            if (found != null) return found
        }
        return EmptyParticle.INSTANCE
    }

    private fun resolveVanilla(name: String): SpawnableParticle {
        val particle = try {
            Particle.valueOf(name.uppercase())
        } catch (_: IllegalArgumentException) {
            return EmptyParticle.INSTANCE
        }
        return SimpleSpawnableParticle(
            particle = particle,
            countExpr = ParticleExpression.Constant(1.0),
            spreadXExpr = ParticleExpression.Constant(0.0),
            spreadYExpr = ParticleExpression.Constant(0.0),
            spreadZExpr = ParticleExpression.Constant(0.0),
            speedExpr = ParticleExpression.Constant(0.0),
            force = false,
            dustSizeExpr = ParticleExpression.Constant(1.0),
            dustColor = Color.WHITE,
            dustTransitionTo = null,
            block = null,
            item = null,
            configuredAudience = ParticleAudience.DEFAULT,
            vars = ParticleVars.EMPTY,
            fieldVarNames = emptyList()
        )
    }

    private fun resolveDust(hex: String): SpawnableParticle? {
        val n = hex.toIntOrNull(16) ?: return null
        val color = try { Color.fromRGB(n) } catch (_: Throwable) { return null }
        return SimpleSpawnableParticle(
            particle = Particle.DUST,
            countExpr = ParticleExpression.Constant(1.0),
            spreadXExpr = ParticleExpression.Constant(0.0),
            spreadYExpr = ParticleExpression.Constant(0.0),
            spreadZExpr = ParticleExpression.Constant(0.0),
            speedExpr = ParticleExpression.Constant(0.0),
            force = false,
            dustSizeExpr = ParticleExpression.Constant(1.0),
            dustColor = color,
            dustTransitionTo = null,
            block = null,
            item = null,
            configuredAudience = ParticleAudience.DEFAULT,
            vars = ParticleVars.EMPTY,
            fieldVarNames = emptyList()
        )
    }
}