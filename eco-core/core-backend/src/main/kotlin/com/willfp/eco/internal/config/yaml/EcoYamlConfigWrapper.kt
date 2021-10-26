package com.willfp.eco.internal.config.yaml

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.StringUtils
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import java.io.StringReader
import java.util.concurrent.ConcurrentHashMap

@Suppress("UNCHECKED_CAST")
open class EcoYamlConfigWrapper<T : ConfigurationSection> : Config {
    lateinit var handle: T
    private val cache = ConcurrentHashMap<String, Any?>()

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
        return subsection ?: EcoYamlConfigSection(YamlConfiguration())
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

    override fun getInts(path: String): MutableList<Int> {
        return if (cache.containsKey(path)) {
            (cache[path] as MutableList<Int>).toMutableList()
        } else {
            cache[path] = if (has(path)) ArrayList(handle.getIntegerList(path)) else mutableListOf<Int>()
            getInts(path)
        }
    }

    override fun getIntsOrNull(path: String): MutableList<Int>? {
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

    override fun getBools(path: String): MutableList<Boolean> {
        return if (cache.containsKey(path)) {
            (cache[path] as MutableList<Boolean>).toMutableList()
        } else {
            cache[path] =
                if (has(path)) ArrayList(handle.getBooleanList(path)) else mutableListOf<Boolean>()
            getBools(path)
        }
    }

    override fun getBoolsOrNull(path: String): MutableList<Boolean>? {
        return if (has(path)) {
            getBools(path)
        } else {
            null
        }
    }

    override fun getString(
        path: String,
        format: Boolean,
        option: StringUtils.FormatOption
    ): String {
        if (format && option == StringUtils.FormatOption.WITHOUT_PLACEHOLDERS) {
            return if (cache.containsKey("$path\$FMT")) {
                cache["$path\$FMT"] as String
            } else {
                val string: String = handle.getString(path, "")!!
                cache["$path\$FMT"] = StringUtils.format(string, option)
                getString(path, format, option)
            }
        } else {
            val string = if (cache.containsKey(path)) {
                cache[path] as String
            } else {
                cache[path] = handle.getString(path, "")!!
                getString(path, format, option)
            }

            return if (format) StringUtils.format(string) else string
        }
    }

    override fun getStringOrNull(
        path: String,
        format: Boolean,
        option: StringUtils.FormatOption
    ): String? {
        return if (has(path)) {
            getString(path, format, option)
        } else {
            null
        }
    }

    override fun getStrings(
        path: String,
        format: Boolean,
        option: StringUtils.FormatOption
    ): MutableList<String> {
        if (format && option == StringUtils.FormatOption.WITHOUT_PLACEHOLDERS) {
            return if (cache.containsKey("$path\$FMT")) {
                (cache["$path\$FMT"] as MutableList<String>).toMutableList()
            } else {
                val list = if (has(path)) handle.getStringList(path) else mutableListOf<String>()
                cache["$path\$FMT"] = StringUtils.formatList(list, option)
                getStrings(path, true, option)
            }
        } else {
            val strings = if (cache.containsKey(path)) {
                (cache[path] as MutableList<String>).toMutableList()
            } else {
                cache[path] =
                    if (has(path)) ArrayList(handle.getStringList(path)) else mutableListOf<String>()
                getStrings(path, false, option)
            }

            return if (format) {
                StringUtils.formatList(strings, StringUtils.FormatOption.WITH_PLACEHOLDERS)
            } else {
                strings
            }
        }
    }

    override fun getStringsOrNull(
        path: String,
        format: Boolean,
        option: StringUtils.FormatOption
    ): MutableList<String>? {
        return if (has(path)) {
            getStrings(path, format, option)
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

    override fun getDoubles(path: String): MutableList<Double> {
        return if (cache.containsKey(path)) {
            (cache[path] as MutableList<Double>).toMutableList()
        } else {
            cache[path] = if (has(path)) ArrayList(handle.getDoubleList(path)) else emptyList<Double>()
            getDoubles(path)
        }
    }

    override fun getDoublesOrNull(path: String): MutableList<Double>? {
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