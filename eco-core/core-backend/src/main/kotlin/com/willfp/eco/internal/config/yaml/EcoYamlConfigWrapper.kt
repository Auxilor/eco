package com.willfp.eco.internal.config.yaml

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.StaticPlaceholder
import com.willfp.eco.internal.config.ensureConfigSerializable
import com.willfp.eco.util.StringUtils
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import java.io.StringReader
import java.util.Optional

@Suppress("UNCHECKED_CAST")
open class EcoYamlConfigWrapper<T : ConfigurationSection> : Config {
    lateinit var handle: T
    var injections = mutableListOf<StaticPlaceholder>()

    private val cache: Cache<String, Optional<Any>> = Caffeine.newBuilder()
        .build()

    protected fun init(config: T): Config {
        handle = config
        return this
    }

    override fun toPlaintext(): String {
        val temp = YamlConfiguration()
        for (key in handle.getKeys(true)) {
            temp[key] = handle[key]
        }
        return temp.saveToString()
    }

    override fun clearCache() {
        cache.invalidateAll()
    }

    override fun has(path: String): Boolean {
        return handle.contains(path)
    }

    override fun getKeys(deep: Boolean): List<String> {
        return ArrayList(handle.getKeys(deep))
    }

    override fun get(path: String): Any? {
        return handle[path]
    }

    override fun set(
        path: String,
        obj: Any?
    ) {
        this.clearCache()
        handle[path] = obj?.ensureConfigSerializable()
    }

    override fun getSubsectionOrNull(path: String): Config? {
        return cache.get(path) {
            val raw = handle.getConfigurationSection(it)
            if (raw == null) {
                return@get Optional.empty()
            } else {
                return@get Optional.of(EcoYamlConfigSection(raw, injections))
            }
        }.orElse(null) as? Config
    }

    override fun getIntOrNull(path: String): Int? {
        return (cache.get(path) {
            if (!handle.contains(it)) {
                return@get Optional.empty()
            }
            val raw = handle.getDouble(it).toInt()
            Optional.of(raw)
        }.orElse(null) as? Number)?.toInt()
    }

    override fun getIntsOrNull(path: String): List<Int>? {
        return (cache.get(path) {
            if (!handle.contains(it)) {
                return@get Optional.empty()
            }
            val raw = handle.getIntegerList(it)
            Optional.of(raw)
        }.orElse(null) as? Iterable<Number>)?.toList()?.map { it.toInt() }
    }

    override fun getBoolOrNull(path: String): Boolean? {
        return cache.get(path) {
            if (!handle.contains(it)) {
                return@get Optional.empty()
            }
            val raw = handle.getBoolean(it)
            Optional.of(raw)
        }.orElse(null) as? Boolean
    }

    override fun getBoolsOrNull(path: String): List<Boolean>? {
        return (cache.get(path) {
            if (!handle.contains(it)) {
                return@get Optional.empty()
            }
            val raw = handle.getBooleanList(it)
            Optional.of(raw)
        }.orElse(null) as? Iterable<Boolean>)?.toList()
    }

    override fun getStringOrNull(
        path: String,
        format: Boolean,
        option: StringUtils.FormatOption
    ): String? {
        val string = cache.get(path) {
            if (!handle.contains(it)) {
                return@get Optional.empty()
            }
            val raw = handle.getString(it)
            Optional.ofNullable(raw)
        }.orElse(null) as? String ?: return null

        return if (format) StringUtils.format(string, option) else string
    }

    override fun getStringsOrNull(
        path: String,
        format: Boolean,
        option: StringUtils.FormatOption
    ): List<String>? {
        val strings = (cache.get(path) {
            if (!handle.contains(it)) {
                return@get Optional.empty()
            }
            val raw = handle.getStringList(it)
            Optional.of(raw)
        }.orElse(null) as? Iterable<String>)?.toList() ?: return null

        return if (format) StringUtils.formatList(strings, option) else strings
    }

    override fun getDoubleOrNull(path: String): Double? {
        return (cache.get(path) {
            if (!handle.contains(it)) {
                return@get Optional.empty()
            }
            val raw = handle.getDouble(it)
            Optional.of(raw)
        }.orElse(null) as? Number)?.toDouble()
    }

    override fun getDoublesOrNull(path: String): List<Double>? {
        return (cache.get(path) {
            if (!handle.contains(it)) {
                return@get Optional.empty()
            }
            val raw = handle.getIntegerList(it)
            Optional.of(raw)
        }.orElse(null) as? Iterable<Number>)?.toList()?.map { it.toDouble() }
    }

    override fun getSubsectionsOrNull(path: String): List<Config>? {
        return (cache.get(path) {
            val raw = handle.getMapList(it)
            val configs = mutableListOf<Config>()

            for (map in raw) {
                val temp = YamlConfiguration() // Empty config
                temp.createSection("temp", map)
                configs.add(EcoYamlConfigSection(temp.getConfigurationSection("temp")!!, injections))
            }

            if (configs.isEmpty()) {
                return@get Optional.empty()
            } else {
                return@get Optional.of(configs)
            }
        }.orElse(null) as? Iterable<Config>)?.toList()
    }

    override fun injectPlaceholders(placeholders: Iterable<StaticPlaceholder>) {
        injections.removeIf { placeholders.any { placeholder -> it.identifier == placeholder.identifier } }
        injections.addAll(placeholders)
        this.clearCache()
    }

    override fun getInjectedPlaceholders(): List<StaticPlaceholder> {
        return injections.toList()
    }

    override fun clearInjectedPlaceholders() {
        injections.clear()
        this.clearCache()
    }

    override fun getType(): ConfigType {
        return ConfigType.JSON
    }

    override fun toMap(): MutableMap<String, Any?> {
        return handle.getValues(true)
    }

    override fun clone(): Config {
        return EcoYamlConfigSection(
            YamlConfiguration.loadConfiguration(
                StringReader(
                    toPlaintext()
                )
            ),
            injections
        )
    }
}
