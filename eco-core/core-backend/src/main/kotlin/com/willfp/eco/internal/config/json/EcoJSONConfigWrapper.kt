package com.willfp.eco.internal.config.json

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.eco.util.StringUtils
import org.apache.commons.lang.Validate
import java.util.*

@Suppress("UNCHECKED_CAST")
open class EcoJSONConfigWrapper : JSONConfig {
    val handle: Gson = GsonBuilder().setPrettyPrinting().create()

    val values: MutableMap<String, Any?> = HashMap()

    private val cache: MutableMap<String, Any> = HashMap()

    fun init(values: Map<String, Any?>) {
        this.values.clear()
        this.values.putAll(values)
    }

    override fun clearCache() {
        cache.clear()
    }

    override fun toPlaintext(): String {
        return this.handle.toJson(this.values)
    }

    override fun has(path: String): Boolean {
        return getOfKnownType(path, Any::class.java) != null
    }

    private fun <T: Any?> getOfKnownType(
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
                EcoJSONConfigSection((values[closestPath] as Map<String, Any?>?)!!)
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
                val section = EcoJSONConfigSection((values[key] as Map<String, Any?>?)!!)
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
            val section = EcoJSONConfigSection((values[closestPath] as Map<String, Any?>?)!!)
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
        val subsection = getSubsectionOrNull(path)
        Validate.notNull(subsection)
        return subsection!!
    }

    override fun getSubsectionOrNull(path: String): JSONConfig? {
        return if (values.containsKey(path)) {
            val subsection = values[path] as Map<String, Any>?
            EcoJSONConfigSection(subsection!!)
        } else {
            null
        }
    }

    override fun getSubsections(path: String): List<JSONConfig> {
        val subsections = getSubsectionsOrNull(path)
        return subsections ?: mutableListOf()
    }

    override fun getSubsectionsOrNull(path: String): List<JSONConfig>? {
        val maps = getOfKnownType(path, Any::class.java) as List<Map<String, Any>>?
            ?: return null
        val configs: MutableList<JSONConfig> = mutableListOf()
        for (map in maps) {
            configs.add(EcoJSONConfigSection(map))
        }
        return configs
    }

    override fun getInt(path: String): Int {
        return (getOfKnownType(path, Double::class.java) ?: 0.0).toInt()
    }

    override fun getIntOrNull(path: String): Int? {
        return if (has(path)) {
            getInt(path)
        } else {
            null
        }
    }

    override fun getInt(
        path: String,
        def: Int
    ): Int {
        return Objects.requireNonNullElse(getOfKnownType(path, Int::class.java), def)
    }

    override fun getInts(path: String): List<Int> {
        return Objects.requireNonNullElse(getOfKnownType(path, Any::class.java), ArrayList<Any>()) as List<Int>
    }

    override fun getIntsOrNull(path: String): List<Int>? {
        return if (has(path)) {
            getInts(path)
        } else {
            null
        }
    }

    override fun getBool(path: String): Boolean {
        return Objects.requireNonNullElse(getOfKnownType(path, Boolean::class.java), false)
    }

    override fun getBoolOrNull(path: String): Boolean? {
        return if (has(path)) {
            getBool(path)
        } else {
            null
        }
    }

    override fun getBools(path: String): List<Boolean> {
        return Objects.requireNonNullElse(getOfKnownType(path, Any::class.java), ArrayList<Any>()) as List<Boolean>
    }

    override fun getBoolsOrNull(path: String): List<Boolean>? {
        return if (has(path)) {
            getBools(path)
        } else {
            null
        }
    }

    override fun getString(path: String): String {
        return getString(path, true)
    }

    override fun getString(
        path: String,
        format: Boolean
    ): String {
        val string = getOfKnownType(path, String::class.java) ?: ""
        return if (format) StringUtils.format(string) else string
    }

    override fun getStringOrNull(path: String): String? {
        return if (has(path)) {
            getString(path)
        } else {
            null
        }
    }

    override fun getStringOrNull(
        path: String,
        format: Boolean
    ): String? {
        return if (has(path)) {
            getString(path, format)
        } else {
            null
        }
    }

    override fun getStrings(path: String): List<String> {
        return getStrings(path, true)
    }

    override fun getStrings(
        path: String,
        format: Boolean
    ): List<String> {
        val strings =
            Objects.requireNonNullElse(getOfKnownType(path, Any::class.java), ArrayList<Any>()) as List<String>
        return if (format) StringUtils.formatList(strings) else strings
    }

    override fun getStringsOrNull(path: String): List<String>? {
        return if (has(path)) {
            getStrings(path)
        } else {
            null
        }
    }

    override fun getStringsOrNull(
        path: String,
        format: Boolean
    ): List<String>? {
        return if (has(path)) {
            getStrings(path, format)
        } else {
            null
        }
    }

    override fun getDouble(path: String): Double {
        return Objects.requireNonNullElse(getOfKnownType(path, Double::class.java), 0.0)
    }

    override fun getDoubleOrNull(path: String): Double? {
        return if (has(path)) {
            getDouble(path)
        } else {
            null
        }
    }

    override fun getDoubles(path: String): List<Double> {
        return Objects.requireNonNullElse(getOfKnownType(path, Any::class.java), ArrayList<Any>()) as List<Double>
    }

    override fun getDoublesOrNull(path: String): List<Double>? {
        return if (has(path)) {
            getDoubles(path)
        } else {
            null
        }
    }

    override fun clone(): JSONConfig {
        return EcoJSONConfigSection(HashMap<String, Any>(this.values))
    }
}
