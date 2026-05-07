package com.willfp.eco.internal.particle

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.particle.ParticleOrigin
import com.willfp.eco.core.particle.Particles
import com.willfp.eco.core.particle.SpawnableParticle
import com.willfp.eco.internal.particle.config.ParticleConfigException
import com.willfp.eco.internal.particle.config.ParticleConfigLoader
import com.willfp.eco.internal.particle.legacy.LegacyStringResolver
import com.willfp.eco.internal.particle.primitives.SimpleSpawnableParticle
import org.bukkit.NamespacedKey
import java.util.concurrent.TimeUnit
import java.util.function.BiConsumer
import java.util.function.Function

/**
 * Installs the backend service hooks onto [Particles] and provides a single
 * entry point for the spigot plugin to call at startup.
 */
object ParticleSystemBootstrap {

    private val loader = ParticleConfigLoader()

    private val stringCache: LoadingCache<String, SpawnableParticle> = Caffeine.newBuilder()
        .expireAfterAccess(10, TimeUnit.MINUTES)
        .build { key ->
            if (key.contains(':')) {
                val parts = key.split(':', limit = 2)
                val nsKey = try { NamespacedKey(parts[0].lowercase(), parts[1].lowercase()) } catch (_: Throwable) { null }
                if (nsKey != null) {
                    val found = Particles.find(nsKey)
                    if (found != null) return@build found
                }
            }
            LegacyStringResolver.resolve(key)
        }

    fun install(eco: EcoPlugin) {
        SimpleSpawnableParticle.setPlugin(eco)

        Particles.installStringResolver { key ->
            stringCache.get(key)
        }

        Particles.installConfigLoader { plugin, configKey ->
            loadParticlesFromConfig(plugin, configKey)
        }

        Particles.installCacheInvalidator { stringCache.invalidateAll() }
    }

    private fun loadParticlesFromConfig(plugin: EcoPlugin, configKey: String) {
        val config = readConfig(plugin, configKey)
        if (config == null) {
            plugin.logger.warning("particles config '$configKey' not found for plugin ${plugin.name}")
            return
        }

        for (entryName in config.keys) {
            val key = plugin.createNamespacedKey(entryName.lowercase())
            val existing = Particles.registryView()[key]
            if (existing != null && existing.origin() == ParticleOrigin.PLUGIN) {
                plugin.logger.fine("config entry $key overridden by plugin registration")
                continue
            }
            try {
                @Suppress("UNCHECKED_CAST")
                val raw = config[entryName] as? Map<String, Any>
                    ?: throw ParticleConfigException("Entry '$entryName' must be a map")
                val sp = loader.load(key, raw, plugin)
                Particles.registerInternal(key, sp, ParticleOrigin.CONFIG, plugin)
            } catch (ex: Throwable) {
                plugin.logger.severe("Failed to load particle '$entryName': ${ex.message}")
            }
        }
    }

    private fun readConfig(plugin: EcoPlugin, configKey: String): Map<String, Any?>? {
        return try {
            val stream = plugin.getResource("$configKey.yml") ?: return null
            val yaml = org.yaml.snakeyaml.Yaml()
            stream.use {
                @Suppress("UNCHECKED_CAST")
                yaml.load<Any?>(it) as? Map<String, Any?>
            }
        } catch (_: Throwable) {
            null
        }
    }
}