package com.willfp.eco.internal.config

import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.InjectablePlaceholder
import com.willfp.eco.core.placeholder.StaticPlaceholder
import com.willfp.eco.util.StringUtils
import org.bukkit.configuration.file.YamlConfiguration
import java.util.concurrent.ConcurrentHashMap

@Suppress("UNCHECKED_CAST")
open class EcoConfig(
    private val configType: ConfigType
) : Config {
    private val values = ConcurrentHashMap<String, Any?>()

    @Transient
    var injections = mutableListOf<InjectablePlaceholder>()

    fun init(values: Map<String, Any?>) {
        this.values.clear()
        this.values.putAll(values.normalizeToConfig(this.type))
    }

    override fun toPlaintext(): String {
        return configType.toString(this.values)
    }

    override fun has(path: String): Boolean {
        return get(path) != null
    }

    override fun getKeys(deep: Boolean): List<String> {
        return if (deep) {
            recurseKeys(mutableSetOf(), "")
        } else {
            values.keys.toList()
        }
    }

    override fun recurseKeys(current: MutableSet<String>, root: String): List<String> {
        val list = mutableSetOf<String>()
        for (key in getKeys(false)) {
            list.add("$root$key")
            val found = get(key)
            if (found is Config) {
                list.addAll(found.recurseKeys(current, "$root$key."))
            }
        }

        return list.toList()
    }

    override fun get(path: String): Any? {
        val nearestPath = path.split(".")[0]

        if (path.contains(".")) {
            val remainingPath = path.removePrefix("${nearestPath}.")

            if (remainingPath.isEmpty()) {
                return null
            }

            val first = get(nearestPath)

            return if (first is Config) {
                first.get(remainingPath)
            } else {
                null
            }
        }

        return values[nearestPath]
    }

    override fun set(
        path: String,
        obj: Any?
    ) {
        val nearestPath = path.split(".")[0]

        if (path.contains(".")) {
            val remainingPath = path.removePrefix("${nearestPath}.")

            if (remainingPath.isEmpty()) {
                return
            }

            val section = getSubsection(nearestPath) // Creates a section if null, therefore it can be set.
            section.set(remainingPath, obj)
            values[nearestPath] = section // Set the value
            return
        }

        if (obj == null) {
            values.remove(nearestPath)
        } else {
            values[nearestPath] = obj.constrainConfigTypes(type)
        }
    }

    override fun getSubsection(path: String): Config {
        return getSubsectionOrNull(path) ?: EcoConfigSection(type, injections = injections)
    }

    override fun getSubsectionOrNull(path: String): Config? {
        return (get(path) as? Config)?.apply { this.addInjectablePlaceholder(injections) }
    }

    override fun getSubsectionsOrNull(path: String): List<Config>? {
        return (get(path) as? Iterable<Config>)
            ?.map { it.apply { this.addInjectablePlaceholder(injections) } }
            ?.toList()
    }

    override fun getType(): ConfigType {
        return configType
    }

    override fun getIntOrNull(path: String): Int? {
        return (get(path) as? Number)?.toInt()
    }

    override fun getIntsOrNull(path: String): List<Int>? {
        return (get(path) as? Iterable<Number>)?.map { it.toInt() }
    }

    override fun getBoolOrNull(path: String): Boolean? {
        return get(path) as? Boolean
    }

    override fun getBoolsOrNull(path: String): List<Boolean>? {
        return (get(path) as? Iterable<Boolean>)?.toList()
    }

    override fun getStringOrNull(
        path: String,
        format: Boolean,
        option: StringUtils.FormatOption
    ): String? {
        var string = get(path)?.toString() ?: return null
        if (format && option == StringUtils.FormatOption.WITH_PLACEHOLDERS) {
            for (injection in placeholderInjections) {
                if (injection is StaticPlaceholder) {
                    string = string.replace("%${injection.identifier}%", injection.value)
                }
            }
        }
        return if (format) StringUtils.format(string, option) else string
    }

    override fun getStringsOrNull(
        path: String,
        format: Boolean,
        option: StringUtils.FormatOption
    ): List<String>? {
        val strings = (get(path) as? Iterable<*>)
            ?.map { it?.toString() ?: "" }
            ?.toMutableList() ?: return null
        if (placeholderInjections.isNotEmpty() && format && option == StringUtils.FormatOption.WITH_PLACEHOLDERS) {
            strings.replaceAll {
                var string = it
                for (injection in placeholderInjections) {
                    if (injection is StaticPlaceholder) {
                        string = string.replace("%${injection.identifier}%", injection.value)
                    }
                }
                string
            }
        }
        return if (format) StringUtils.formatList(strings, option) else strings
    }

    override fun getDoubleOrNull(path: String): Double? {
        return (get(path) as? Number)?.toDouble()
    }

    override fun getDoublesOrNull(path: String): List<Double>? {
        return (get(path) as? Iterable<Number>)?.map { it.toDouble() }
    }

    override fun addInjectablePlaceholder(placeholders: Iterable<InjectablePlaceholder>) {
        injections.removeIf { placeholders.any { placeholder -> it.identifier == placeholder.identifier } }
        injections.addAll(placeholders)
    }

    override fun getPlaceholderInjections(): List<InjectablePlaceholder> {
        return injections.toList()
    }

    override fun clearInjectedPlaceholders() {
        injections.clear()
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

    override fun toString(): String {
        return this.toPlaintext()
    }
}
