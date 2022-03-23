package com.willfp.eco.internal.config

import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.StaticPlaceholder
import com.willfp.eco.util.StringUtils
import org.bukkit.configuration.file.YamlConfiguration
import java.util.concurrent.ConcurrentHashMap

@Suppress("UNCHECKED_CAST")
open class EcoConfig(
    private val configType: ConfigType
) : Config {
    val values = ConcurrentHashMap<String, Any?>()
    var injections = mutableListOf<StaticPlaceholder>()

    fun init(values: Map<String, Any?>) {
        this.values.clear()
        this.values.putAll(values)
    }

    override fun clearCache() {
        // No cache
    }

    override fun toPlaintext(): String {
        return configType.handler.toString(this.values)
    }

    override fun has(path: String): Boolean {
        return getOfKnownType(path, Any::class.java) != null
    }

    private fun <T> getOfKnownType(
        path: String,
        clazz: Class<T>
    ): T? {
        val nearestPath = path.split(".")[0]

        if (path.contains(".")) {
            val remainingPath = path.removePrefix("${nearestPath}.")
            return getOfKnownType(remainingPath, clazz)
        }

        val found = values[nearestPath] ?: return null

        return if (found is Map<*, *>) {
            val rawSection = found as Map<String, Any?>
            EcoConfigSection(type, rawSection, injections) as? T?
        } else if (found is Iterable<*>) {
            val first = found.firstOrNull() // Type erasure
            if (first is Map<*, *>) {
                println("Amogus? $found")
                val rawSections = found as Iterable<Map<String, Any?>>
                rawSections.map { EcoConfigSection(type, it, injections) }
            } else {
                found
            }
        } else {
            found
        } as? T?
    }

    private fun setRecursively(
        path: String,
        obj: Any?
    ) {
        this.clearCache()
        val nearestPath = path.split(".")[0]

        if (path.contains(".")) {
            val remainingPath = path.removePrefix("${nearestPath}.")
            val section = getOfKnownType(nearestPath, Any::class.java)
            if (section == null) {
                values[nearestPath] = mutableMapOf<String, Any?>()
                return setRecursively(path, obj)
            } else if (section is EcoConfig) {
                section.setRecursively(remainingPath, obj)
            }
        }

        values[nearestPath] = when (obj) {
            is Config -> obj.toMap()
            is Collection<*> -> {
                val first = obj.firstOrNull()
                if (first is Config) {
                    @Suppress("UNCHECKED_CAST")
                    obj as Collection<Config>
                    obj.map { it.toMap() }.toMutableList()
                } else if (obj.isEmpty()) {
                    mutableListOf() // Don't use EmptyList, causes anchors as they have the same reference
                } else {
                    obj.toMutableList()
                }
            }
            else -> obj
        }
    }

    override fun getKeys(deep: Boolean): List<String> {
        return if (deep) {
            recurseKeys(mutableSetOf()).toList()
        } else {
            values.keys.filterIsInstance<String>().toList()
        }
    }

    protected fun recurseKeys(
        list: MutableSet<String>,
        root: String = ""
    ): Set<String> {
        for (key in getKeys(false)) {
            list.add("$root$key")
            val found = get(key)
            if (found is EcoConfig) {
                list.addAll(found.recurseKeys(list, "$root$key."))
            }
        }

        return list
    }

    override fun get(path: String): Any? {
        return getOfKnownType(path, Any::class.java)
    }

    override fun set(
        path: String,
        obj: Any?
    ) {
        setRecursively(path, obj)
    }

    override fun getSubsection(path: String): Config {
        return getSubsectionOrNull(path) ?: EcoConfigSection(type, mutableMapOf(), injections)
    }

    override fun getSubsectionOrNull(path: String): Config? {
        return getOfKnownType(path, Config::class.java)
    }

    override fun getSubsectionsOrNull(path: String): List<Config>? {
        return (getOfKnownType(path, Iterable::class.java) as? Iterable<Config>)?.toList()
    }

    override fun getType(): ConfigType {
        return configType
    }

    override fun getIntOrNull(path: String): Int? {
        return getOfKnownType(path, Number::class.java)?.toInt()
    }

    override fun getIntsOrNull(path: String): MutableList<Int>? {
        return (getOfKnownType(path, Iterable::class.java) as? Iterable<Int>)?.toMutableList()
    }

    override fun getBoolOrNull(path: String): Boolean? {
        return getOfKnownType(path, Boolean::class.java)
    }

    override fun getBoolsOrNull(path: String): MutableList<Boolean>? {
        return (getOfKnownType(path, Iterable::class.java) as? Iterable<Boolean>)?.toMutableList()
    }

    override fun getStringOrNull(
        path: String,
        format: Boolean,
        option: StringUtils.FormatOption
    ): String? {
        val string = getOfKnownType(path, String::class.java) ?: return null
        return if (format) StringUtils.format(string, option) else string
    }

    override fun getStringsOrNull(
        path: String,
        format: Boolean,
        option: StringUtils.FormatOption
    ): List<String>? {
        val strings = (getOfKnownType(path, Iterable::class.java) as? Iterable<String>)?.toList() ?: return null
        return if (format) StringUtils.formatList(strings, option) else strings
    }

    override fun getDoubleOrNull(path: String): Double? {
        return getOfKnownType(path, Number::class.java)?.toDouble()
    }

    override fun getDoublesOrNull(path: String): MutableList<Double>? {
        return (getOfKnownType(path, Iterable::class.java) as? Iterable<Double>)?.toMutableList()
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

    override fun toMap(): MutableMap<String, Any?> {
        return values.toMutableMap()
    }

    override fun toBukkit(): YamlConfiguration {
        val temp = YamlConfiguration()
        temp.createSection("temp", this.values.toMap())
        val section = temp.getConfigurationSection("temp")!!

        val bukkit = YamlConfiguration()
        for (key in section.getKeys(true)) {
            bukkit.set(key, section.get(key))
        }
        return bukkit
    }

    override fun clone(): Config {
        return EcoConfigSection(type, this.values.toMutableMap(), injections)
    }
}
