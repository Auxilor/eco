package com.willfp.eco.internal.particle.config

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.particle.ParticleAudience
import com.willfp.eco.core.particle.Particles
import com.willfp.eco.core.particle.SpawnableParticle
import com.willfp.eco.core.particle.impl.EmptyParticle
import com.willfp.eco.internal.particle.ParticleExpression
import com.willfp.eco.internal.particle.ParticleExpressionCompiler
import com.willfp.eco.internal.particle.ParticleScope
import com.willfp.eco.internal.particle.ParticleVars
import com.willfp.eco.internal.particle.PrimitiveParticleType
import com.willfp.eco.internal.particle.primitives.CompositeSpawnableParticle
import com.willfp.eco.internal.particle.primitives.IterateSpawnableParticle
import com.willfp.eco.internal.particle.primitives.SimpleSpawnableParticle
import com.willfp.eco.internal.particle.primitives.TickSpawnableParticle
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Particle

/**
 * Walks a YAML map and produces a [SpawnableParticle].
 *
 * Reserved variable names by primitive:
 *   - simple:    none
 *   - tick:      `t` (per-tick)
 *   - iterate:   `i`, `n` (per-point)
 *   - composite: none
 */
internal class ParticleConfigLoader {

    /**
     * Top-level entry: load a single YAML map into a SpawnableParticle.
     * If the map has a `parameters:` field, it's a preset.
     */
    fun load(
        key: NamespacedKey,
        map: Map<String, Any>,
        plugin: EcoPlugin
    ): SpawnableParticle {
        val parameters = map["parameters"]
        if (parameters is Map<*, *>) {
            val params = parameters.entries.associate {
                it.key.toString() to (it.value ?: error("preset $key parameter ${it.key} has no default"))
            }
            val body = map.filterKeys { it != "parameters" }
            return PresetSpawnableParticle(key, params, body, plugin) { this }
        }
        return loadEntry(map, plugin, ParticleScope())
    }

    /**
     * Load an entry body. Called recursively for inline child maps and from preset instantiation.
     */
    fun loadEntry(
        body: Map<String, Any?>,
        plugin: EcoPlugin,
        scope: ParticleScope,
        visibleReserved: List<String> = emptyList(),
        outerVars: List<String> = emptyList()
    ): SpawnableParticle {
        val typeKey = (body["type"] ?: throw ParticleConfigException("Missing required field 'type'")).toString()
        val primitive = PrimitiveParticleType.fromKey(typeKey)
        if (primitive != null) {
            return loadPrimitive(primitive, body, plugin, scope, visibleReserved, outerVars)
        }

        val refKey = parseNamespacedKey(typeKey, plugin)
            ?: throw ParticleConfigException("Unknown particle type '$typeKey'")
        val registered = Particles.find(refKey)
            ?: throw ParticleConfigException("Unregistered preset: $refKey")
        if (registered is PresetSpawnableParticle) {
            val overrides = body.filterKeys { it != "type" }.mapValues { resolveOverride(it.value, scope) }
            return registered.instantiate(overrides, scope.resolving(refKey))
        }
        return registered
    }

    private fun loadPrimitive(
        type: PrimitiveParticleType,
        body: Map<String, Any?>,
        plugin: EcoPlugin,
        scope: ParticleScope,
        visibleReserved: List<String>,
        outerVars: List<String>
    ): SpawnableParticle = when (type) {
        PrimitiveParticleType.SIMPLE -> loadSimple(body, plugin, scope, visibleReserved, outerVars)
        PrimitiveParticleType.TICK -> loadTick(body, plugin, scope, visibleReserved, outerVars)
        PrimitiveParticleType.ITERATE -> loadIterate(body, plugin, scope, visibleReserved, outerVars)
        PrimitiveParticleType.COMPOSITE -> loadComposite(body, plugin, scope, visibleReserved, outerVars)
    }

    private fun loadSimple(
        body: Map<String, Any?>,
        plugin: EcoPlugin,
        scope: ParticleScope,
        visibleReserved: List<String>,
        outerVars: List<String>
    ): SpawnableParticle {
        val particleStr = stringField(body, "particle", scope)
            ?: throw ParticleConfigException("simple: missing required field 'particle'")
        val particle = parseParticle(particleStr)
            ?: throw ParticleConfigException("simple: unknown particle '$particleStr'")

        val vars = compileVars(body["vars"], scope, visibleReserved, outerVars)
        val varsBlockNames = (body["vars"] as? Map<*, *>)?.keys?.map { it.toString() } ?: emptyList()
        val fieldVarNames = visibleReserved + outerVars + varsBlockNames

        val countExpr = compileNumeric(body, "count", "1", scope, fieldVarNames)
        val spreadX = compileNumeric(body, "spread_x", "0", scope, fieldVarNames)
        val spreadY = compileNumeric(body, "spread_y", "0", scope, fieldVarNames)
        val spreadZ = compileNumeric(body, "spread_z", "0", scope, fieldVarNames)
        val speed = compileNumeric(body, "speed", "0", scope, fieldVarNames)
        val force = (body["force"] as? Boolean) ?: false
        val dustSize = compileNumeric(body, "dust_size", "1", scope, fieldVarNames)
        val dustColor = parseColor(stringField(body, "dust_color", scope)) ?: Color.WHITE
        val dustTransition = stringField(body, "dust_transition", scope)?.let { parseColor(it) }
        val block = stringField(body, "block", scope)?.let { Material.matchMaterial(it.uppercase()) }
        val item: org.bukkit.inventory.ItemStack? = stringField(body, "item", scope)?.let {
            try { com.willfp.eco.core.items.Items.lookup(it).item } catch (_: Throwable) { null }
        }
        val audience = parseAudience(body["audience"], scope)

        return SimpleSpawnableParticle(
            particle, countExpr, spreadX, spreadY, spreadZ, speed, force,
            dustSize, dustColor, dustTransition, block, item,
            audience, vars, fieldVarNames
        )
    }

    private fun loadTick(
        body: Map<String, Any?>,
        plugin: EcoPlugin,
        scope: ParticleScope,
        visibleReserved: List<String>,
        outerVars: List<String>
    ): SpawnableParticle {
        val vars = compileVars(body["vars"], scope, visibleReserved + listOf("t"), outerVars)
        val varsBlockNames = (body["vars"] as? Map<*, *>)?.keys?.map { it.toString() } ?: emptyList()
        val startVarNames = visibleReserved + outerVars + varsBlockNames
        val tickVarNames = startVarNames + "t"

        val intervalExpr = compileNumeric(body, "interval", "1", scope, startVarNames)
        val durationExpr = compileNumeric(body, "duration", "-1", scope, startVarNames)
        val ox = compileNumeric(body, "offset_x", "0", scope, tickVarNames)
        val oy = compileNumeric(body, "offset_y", "0", scope, tickVarNames)
        val oz = compileNumeric(body, "offset_z", "0", scope, tickVarNames)
        val audience = parseAudience(body["audience"], scope)
        val child = loadChild(
            body["child"] ?: throw ParticleConfigException("tick: missing required field 'child'"),
            plugin, scope, listOf("t"), startVarNames
        )

        return TickSpawnableParticle(
            plugin, intervalExpr, durationExpr, ox, oy, oz,
            audience, vars, startVarNames, tickVarNames, child
        )
    }

    private fun loadIterate(
        body: Map<String, Any?>,
        plugin: EcoPlugin,
        scope: ParticleScope,
        visibleReserved: List<String>,
        outerVars: List<String>
    ): SpawnableParticle {
        val vars = compileVars(body["vars"], scope, visibleReserved + listOf("i", "n"), outerVars)
        val varsBlockNames = (body["vars"] as? Map<*, *>)?.keys?.map { it.toString() } ?: emptyList()
        val countVarNames = visibleReserved + outerVars + varsBlockNames
        val pointVarNames = countVarNames + listOf("i", "n")

        val countExpr = compileNumeric(body, "count", null, scope, countVarNames)
            ?: throw ParticleConfigException("iterate: missing required field 'count'")
        val ox = compileNumeric(body, "offset_x", "0", scope, pointVarNames)
        val oy = compileNumeric(body, "offset_y", "0", scope, pointVarNames)
        val oz = compileNumeric(body, "offset_z", "0", scope, pointVarNames)
        val audience = parseAudience(body["audience"], scope)
        val child = loadChild(
            body["child"] ?: throw ParticleConfigException("iterate: missing required field 'child'"),
            plugin, scope, listOf("i", "n"), countVarNames
        )

        return IterateSpawnableParticle(countExpr, ox, oy, oz, audience, vars, countVarNames, pointVarNames, child)
    }

    private fun loadComposite(
        body: Map<String, Any?>,
        plugin: EcoPlugin,
        scope: ParticleScope,
        visibleReserved: List<String>,
        outerVars: List<String>
    ): SpawnableParticle {
        val vars = compileVars(body["vars"], scope, visibleReserved, outerVars)
        val varsBlockNames = (body["vars"] as? Map<*, *>)?.keys?.map { it.toString() } ?: emptyList()
        val childVarNames = visibleReserved + outerVars + varsBlockNames

        val rawChildren = body["children"]
            ?: throw ParticleConfigException("composite: missing required field 'children'")
        val list = rawChildren as? List<*>
            ?: throw ParticleConfigException("composite: 'children' must be a list")
        val children = list.map {
            loadChild(it ?: throw ParticleConfigException("composite: null child entry"),
                plugin, scope, emptyList(), childVarNames)
        }
        val audience = parseAudience(body["audience"], scope)
        return CompositeSpawnableParticle(audience, vars, children)
    }

    private fun loadChild(
        raw: Any,
        plugin: EcoPlugin,
        scope: ParticleScope,
        childReserved: List<String>,
        childOuterVars: List<String>
    ): SpawnableParticle {
        return when (val resolved = resolveOverride(raw, scope)) {
            is String -> {
                val sp = Particles.lookup(resolved)
                if (sp === EmptyParticle.INSTANCE && resolved.isNotBlank()) {
                    plugin.logger.warning("Particle child reference '$resolved' did not resolve")
                }
                sp
            }
            is Map<*, *> -> {
                @Suppress("UNCHECKED_CAST")
                loadEntry(resolved as Map<String, Any?>, plugin, scope, childReserved, childOuterVars)
            }
            else -> throw ParticleConfigException("child must be a string or a map, got ${resolved::class.simpleName}")
        }
    }

    private fun compileNumeric(
        body: Map<String, Any?>,
        field: String,
        default: String?,
        scope: ParticleScope,
        varNames: List<String>
    ): ParticleExpression {
        val raw = body[field]
        val str = when {
            raw == null -> default ?: error("missing required field '$field'")
            raw is Number -> raw.toString()
            raw is String -> applySubstitution(raw, scope.parameters)
            else -> throw ParticleConfigException("Field '$field' must be a number or expression string")
        }
        return ParticleExpressionCompiler.compile(str, varNames)
    }

    private fun compileVars(
        raw: Any?,
        scope: ParticleScope,
        visibleReserved: List<String>,
        outerVars: List<String>
    ): ParticleVars {
        if (raw == null) return ParticleVars.EMPTY
        if (raw !is Map<*, *>) throw ParticleConfigException("'vars' must be a map")
        val accumulatedNames = mutableListOf<String>().apply { addAll(visibleReserved); addAll(outerVars) }
        val entries = raw.entries.map { (rawName, rawExpr) ->
            val name = rawName.toString()
            validateVarName(name)
            val str = when (rawExpr) {
                null -> throw ParticleConfigException("var '$name' has null value")
                is Number -> rawExpr.toString()
                is String -> applySubstitution(rawExpr, scope.parameters)
                else -> throw ParticleConfigException("var '$name' must be a number or expression")
            }
            val expr = ParticleExpressionCompiler.compile(str, accumulatedNames.toList())
            accumulatedNames.add(name)
            ParticleVars.Entry(name, expr, accumulatedNames.toList().dropLast(1))
        }
        return ParticleVars(entries)
    }

    private fun validateVarName(name: String) {
        if (!name.matches(Regex("[a-zA-Z_][a-zA-Z0-9_]*"))) {
            throw ParticleConfigException("Invalid var name '$name'")
        }
        if (name in setOf("t", "i", "n", "pi", "e")) {
            throw ParticleConfigException("var name '$name' collides with a reserved identifier")
        }
    }

    private fun applySubstitution(text: String, params: Map<String, Any>): String {
        if (params.isEmpty() || !text.contains('$')) return text
        var result = text
        for ((name, value) in params) {
            val marker = "\$$name"
            val replacement = when (value) {
                is Number -> value.toString()
                is String -> if (value.startsWith("{{") && value.endsWith("}}")) {
                    value.substring(2, value.length - 2)
                } else value
                else -> value.toString()
            }
            result = result.replace(marker, replacement)
        }
        return result
    }

    private fun resolveOverride(raw: Any?, scope: ParticleScope): Any {
        if (raw is String) {
            val singleParam = Regex("^\\$([a-zA-Z_][a-zA-Z0-9_]*)$").matchEntire(raw)
            if (singleParam != null) {
                val pname = singleParam.groupValues[1]
                val v = scope.parameters[pname]
                if (v != null) return v
            }
            return applySubstitution(raw, scope.parameters)
        }
        return raw ?: ""
    }

    private fun stringField(body: Map<String, Any?>, name: String, scope: ParticleScope): String? {
        val raw = body[name] ?: return null
        return applySubstitution(raw.toString(), scope.parameters)
    }

    private fun parseAudience(raw: Any?, scope: ParticleScope): ParticleAudience {
        if (raw == null) return ParticleAudience.DEFAULT
        return when (val resolved = resolveOverride(raw, scope)) {
            is String -> when (resolved.lowercase()) {
                "world" -> ParticleAudience.WORLD
                "player" -> ParticleAudience.CONTEXT_PLAYER
                "default", "inherit" -> ParticleAudience.DEFAULT
                else -> ParticleAudience.DEFAULT
            }
            is Map<*, *> -> {
                val type = resolved["type"]?.toString()
                when (type?.lowercase()) {
                    "within" -> {
                        val r = (resolved["radius"] as? Number)?.toDouble()
                            ?: (resolved["radius"] as? String)?.toDoubleOrNull()
                            ?: 16.0
                        ParticleAudience.within(r)
                    }
                    else -> ParticleAudience.DEFAULT
                }
            }
            else -> ParticleAudience.DEFAULT
        }
    }

    private fun parseColor(s: String?): Color? {
        if (s == null) return null
        val cleaned = s.removePrefix("#")
        val n = cleaned.toIntOrNull(16) ?: return null
        return try { Color.fromRGB(n) } catch (_: Throwable) { null }
    }

    private fun parseParticle(s: String): Particle? {
        val trimmed = s.removePrefix("minecraft:")
        if (trimmed.contains(':')) return null
        return try {
            Particle.valueOf(trimmed.uppercase())
        } catch (_: IllegalArgumentException) { null }
    }

    private fun parseNamespacedKey(s: String, plugin: EcoPlugin): NamespacedKey? {
        return if (s.contains(':')) {
            val parts = s.split(':', limit = 2)
            if (parts.size != 2) null
            else try { NamespacedKey(parts[0], parts[1]) } catch (_: Throwable) { null }
        } else {
            try { NamespacedKey("eco", s) } catch (_: Throwable) { null }
        }
    }
}