package com.willfp.eco.internal.config.yaml

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.StringUtils
import org.apache.commons.lang.Validate
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import java.io.StringReader

@Suppress("UNCHECKED_CAST")
open class EcoYamlConfigWrapper<T : ConfigurationSection> : Config {
    lateinit var handle: T
    private val cache: MutableMap<String, Any?> = HashMap()

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
        cache.clear()
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
        cache.remove(path)
        handle[path] = obj
    }

    override fun getSubsection(path: String): Config {
        val subsection = getSubsectionOrNull(path)
        Validate.notNull(subsection)
        return subsection!!
    }

    override fun getSubsectionOrNull(path: String): Config? {
        return if (cache.containsKey(path)) {
            cache[path] as Config?
        } else {
            val raw = handle.getConfigurationSection(path)
            if (raw == null) {
                cache[path] = null
            } else {
                cache[path] = EcoYamlConfigSection(raw)
            }
            getSubsectionOrNull(path)
        }
    }

    override fun getInt(path: String): Int {
        return if (cache.containsKey(path)) {
            cache[path] as Int
        } else {
            cache[path] = handle.getInt(path, 0)
            getInt(path)
        }
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
        return if (cache.containsKey(path)) {
            cache[path] as Int
        } else {
            cache[path] = handle.getInt(path, def)
            getInt(path)
        }
    }

    override fun getInts(path: String): List<Int> {
        return if (cache.containsKey(path)) {
            cache[path] as List<Int>
        } else {
            cache[path] = if (has(path)) ArrayList(handle.getIntegerList(path)) else ArrayList<Any>()
            getInts(path)
        }
    }

    override fun getIntsOrNull(path: String): List<Int>? {
        return if (has(path)) {
            getInts(path)
        } else {
            null
        }
    }

    override fun getBool(path: String): Boolean {
        return if (cache.containsKey(path)) {
            cache[path] as Boolean
        } else {
            cache[path] = handle.getBoolean(path)
            getBool(path)
        }
    }

    override fun getBoolOrNull(path: String): Boolean? {
        return if (has(path)) {
            getBool(path)
        } else {
            null
        }
    }

    override fun getBools(path: String): List<Boolean> {
        return if (cache.containsKey(path)) {
            cache[path] as List<Boolean>
        } else {
            cache[path] =
                if (has(path)) ArrayList(handle.getBooleanList(path)) else ArrayList<Any>()
            getBools(path)
        }
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
        if (format) {
            return if (cache.containsKey("$path\$FMT")) {
                cache["$path\$FMT"] as String
            } else {
                val string: String = handle.getString(path, "")!!
                cache["$path\$FMT"] = StringUtils.format(string)
                getString(path, format)
            }
        } else {
            return if (cache.containsKey(path)) {
                cache[path] as String
            } else {
                cache[path] = handle.getString(path, "")!!
                getString(path)
            }
        }
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
        if (format) {
            return if (cache.containsKey("$path\$FMT")) {
                cache["$path\$FMT"] as List<String>
            } else {
                cache["$path\$FMT"] = StringUtils.formatList(if (has(path)) ArrayList(handle.getStringList(path)) else ArrayList<String>())
                getStrings(path, true)
            }
        } else {
            return if (cache.containsKey(path)) {
                cache[path] as List<String>
            } else {
                cache[path] =
                    if (has(path)) ArrayList(handle.getStringList(path)) else ArrayList<Any>()
                getStrings(path, false)
            }
        }
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
        return if (cache.containsKey(path)) {
            cache[path] as Double
        } else {
            cache[path] = handle.getDouble(path)
            getDouble(path)
        }
    }

    override fun getDoubleOrNull(path: String): Double? {
        return if (has(path)) {
            getDouble(path)
        } else {
            null
        }
    }

    override fun getDoubles(path: String): List<Double> {
        return if (cache.containsKey(path)) {
            cache[path] as List<Double>
        } else {
            cache[path] = if (has(path)) ArrayList(handle.getDoubleList(path)) else ArrayList<Any>()
            getDoubles(path)
        }
    }

    override fun getDoublesOrNull(path: String): List<Double>? {
        return if (has(path)) {
            getDoubles(path)
        } else {
            null
        }
    }

    override fun clone(): Config {
        return EcoYamlConfigSection(
            YamlConfiguration.loadConfiguration(
                StringReader(
                    toPlaintext()
                )
            )
        )
    }
}