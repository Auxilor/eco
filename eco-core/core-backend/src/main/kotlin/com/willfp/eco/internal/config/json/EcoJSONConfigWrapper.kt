@file:Suppress("DEPRECATION")

package com.willfp.eco.internal.config.json

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.eco.core.placeholder.StaticPlaceholder
import com.willfp.eco.util.StringUtils
import java.util.Objects
import java.util.concurrent.ConcurrentHashMap

@Suppress("UNCHECKED_CAST")
open class EcoJSONConfigWrapper : JSONConfig {
    companion object {
        val gson: Gson = GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create()
    }

    val values = ConcurrentHashMap<String, Any?>()

    private val cache = ConcurrentHashMap<String, Any>()
    var injections = mutableListOf<StaticPlaceholder>()

    fun init(values: Map<String, Any?>) {
        this.values.clear()
        this.values.putAll(values)
    }

    override fun clearCache() {
        cache.clear()
    }

    override fun toPlaintext(): String {
        return gson.toJson(this.values)
    }

    override fun has(path: String): Boolean {
        return getOfKnownType(path, Any::class.java) != null
    }

    private fun <T : Any?> getOfKnownType(
        path: String,
        clazz: Class<T>
    ): T? {
        return getOfKnownType(path, clazz, true)
    }

    protected fun <T> getOfKnownType(
        path: String,
        clazz: Class<T>,
        isBase: Boolean
    ): T? {
        var closestPath = path
        if (cache.containsKey(path) && isBase) {
            return cache[path] as T?
        }
        if (path.contains(".")) {
            val split = path.split("\\.".toRegex()).toTypedArray()
            closestPath = split[0]
        }
        return if (values[closestPath] is Map<*, *> && path != closestPath) {
            val section =
                EcoJSONConfigSection((values[closestPath] as Map<String, Any?>?)!!, injections)
            section.getOfKnownType(path.substring(closestPath.length + 1), clazz, false)
        } else {
            if (values.containsKey(closestPath)) {
                values[closestPath] as T?
            } else {
                null
            }
        }
    }

    override fun getKeys(deep: Boolean): List<String> {
        return if (deep) {
            ArrayList(getDeepKeys(HashSet(), ""))
        } else {
            ArrayList(values.keys)
        }
    }

    protected fun getDeepKeys(
        list: MutableSet<String>,
        root: String
    ): Set<String> {
        for (key in values.keys) {
            list.add(root + key)
            if (values[key] is Map<*, *>) {
                val section = EcoJSONConfigSection((values[key] as Map<String, Any?>?)!!, injections)
                list.addAll(section.getDeepKeys(list, "$root$key."))
            }
        }
        return list
    }

    override fun get(path: String): Any? {
        return getOfKnownType(path, Any::class.java)
    }

    override fun set(
        path: String,
        `object`: Any?
    ) {
        setRecursively(path, `object`)
        clearCache()
    }

    protected fun setRecursively(
        path: String,
        obj: Any?
    ) {
        var closestPath = path
        if (path.contains(".")) {
            val split = path.split("\\.".toRegex()).toTypedArray()
            closestPath = split[0]
        }
        if (values[closestPath] is Map<*, *> && path != closestPath) {
            val section = EcoJSONConfigSection((values[closestPath] as Map<String, Any?>?)!!, injections)
            section.setRecursively(path.substring(closestPath.length + 1), obj)
            values[closestPath] = section.values
        } else {
            var ob = obj
            if (ob is JSONConfig) {
                ob = (obj as EcoJSONConfigWrapper).values
            }
            values[path] = ob
        }
    }

    override fun getSubsection(path: String): JSONConfig {
        return getSubsectionOrNull(path) ?: EcoJSONConfigSection(mutableMapOf(), injections)
    }

    override fun getSubsectionOrNull(path: String): JSONConfig? {
        return if (values.containsKey(path)) {
            val subsection = values[path] as Map<String, Any>
            EcoJSONConfigSection(subsection, injections)
        } else {
            null
        }
    }

    override fun getSubsectionsOrNull(path: String): List<JSONConfig>? {
        val maps = getOfKnownType(path, Any::class.java) as List<Map<String, Any>>?
            ?: return null
        val configs = mutableListOf<JSONConfig>()
        for (map in maps) {
            configs.add(EcoJSONConfigSection(map, injections))
        }
        return configs.toMutableList()
    }

    override fun getIntOrNull(path: String): Int? {
        return getOfKnownType(path, Double::class.java)?.toInt()
    }

    override fun getIntsOrNull(path: String): MutableList<Int>? {
        return (getOfKnownType(path, Any::class.java) as Collection<Int>?)?.toMutableList()
    }

    override fun getBoolOrNull(path: String): Boolean? {
        return getOfKnownType(path, Boolean::class.java)
    }

    override fun getBoolsOrNull(path: String): MutableList<Boolean>? {
        return (getOfKnownType(path, Any::class.java) as Collection<Boolean>?)?.toMutableList()
    }

    override fun getStringOrNull(
        path: String,
        format: Boolean,
        option: StringUtils.FormatOption
    ): String? {
        return if (has(path)) {
            val string = getOfKnownType(path, String::class.java) ?: ""
            return if (format) StringUtils.format(string, option) else string
        } else {
            null
        }
    }

    override fun getStringsOrNull(
        path: String,
        format: Boolean,
        option: StringUtils.FormatOption
    ): MutableList<String>? {
        return if (has(path)) {
            val strings =
                (Objects.requireNonNullElse(
                    getOfKnownType(path, Any::class.java),
                    emptyList<String>()
                ) as List<String>).toMutableList()
            return if (format) StringUtils.formatList(strings, option) else strings
        } else {
            null
        }
    }

    override fun getDoubleOrNull(path: String): Double? {
        return getOfKnownType(path, Double::class.java)
    }

    override fun getDoublesOrNull(path: String): MutableList<Double>? {
        return (getOfKnownType(path, Any::class.java) as Collection<Double>?)?.toMutableList()
    }

    override fun injectPlaceholders(vararg placeholders: StaticPlaceholder) {
        injections.addAll(placeholders)
    }

    override fun getInjectedPlaceholders(): List<StaticPlaceholder> {
        return injections.toList()
    }

    override fun getType(): ConfigType {
        return ConfigType.JSON
    }

    override fun clone(): JSONConfig {
        return EcoJSONConfigSection(this.values.toMutableMap(), injections)
    }
}