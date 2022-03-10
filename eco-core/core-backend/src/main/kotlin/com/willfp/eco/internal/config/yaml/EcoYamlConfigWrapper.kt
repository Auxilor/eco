package com.willfp.eco.internal.config.yaml

import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.StaticPlaceholder
import com.willfp.eco.util.StringUtils
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import java.io.StringReader

@Suppress("UNCHECKED_CAST")
open class EcoYamlConfigWrapper<T : ConfigurationSection> : Config {
    lateinit var handle: T
    private val cache = mutableMapOf<String, Any?>()
    var injections = mutableListOf<StaticPlaceholder>()

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

    override fun getSubsectionOrNull(path: String): Config? {
        return if (cache.containsKey(path)) {
            cache[path] as Config?
        } else {
            val raw = handle.getConfigurationSection(path)
            if (raw == null) {
                cache[path] = null
            } else {
                cache[path] = EcoYamlConfigSection(raw, injections)
            }
            getSubsectionOrNull(path)
        }
    }

    override fun getIntOrNull(path: String): Int? {
        return if (cache.containsKey(path)) {
            (cache[path] as Number).toInt()
        } else {
            if (has(path)) {
                cache[path] = handle.getDouble(path).toInt()
            } else {
                return null
            }
            getIntOrNull(path)
        }
    }

    override fun getIntsOrNull(path: String): MutableList<Int>? {
        return if (cache.containsKey(path)) {
            (cache[path] as Collection<Int>).toMutableList()
        } else {
            if (has(path)) {
                cache[path] = handle.getIntegerList(path).toMutableList()
            } else {
                return null
            }
            getIntsOrNull(path)
        }
    }

    override fun getBoolOrNull(path: String): Boolean? {
        return if (cache.containsKey(path)) {
            cache[path] as Boolean
        } else {
            if (has(path)) {
                cache[path] = handle.getBoolean(path)
            } else {
                return null
            }
            getBoolOrNull(path)
        }
    }

    override fun getBoolsOrNull(path: String): MutableList<Boolean>? {
        return if (cache.containsKey(path)) {
            (cache[path] as Collection<Boolean>).toMutableList()
        } else {
            if (has(path)) {
                cache[path] = handle.getBooleanList(path).toMutableList()
            } else {
                return null
            }
            getBoolsOrNull(path)
        }
    }

    override fun getStringOrNull(
        path: String,
        format: Boolean,
        option: StringUtils.FormatOption
    ): String? {
        return if (has(path)) {
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
        } else {
            null
        }
    }

    override fun getDoubleOrNull(path: String): Double? {
        return if (cache.containsKey(path)) {
            (cache[path] as Number).toDouble()
        } else {
            if (has(path)) {
                cache[path] = handle.getDouble(path)
            } else {
                return null
            }
            getDoubleOrNull(path)
        }
    }

    override fun getDoublesOrNull(path: String): MutableList<Double>? {
        return if (cache.containsKey(path)) {
            (cache[path] as Collection<Double>).toMutableList()
        } else {
            if (has(path)) {
                cache[path] = handle.getDoubleList(path).toMutableList()
            } else {
                return null
            }
            getDoublesOrNull(path)
        }
    }

    override fun getSubsectionsOrNull(path: String): MutableList<out Config>? {
        return if (has(path)) {
            return if (cache.containsKey(path)) {
                (cache[path] as Collection<Config>).toMutableList()
            } else {
                val mapList = ArrayList(handle.getMapList(path)) as List<Map<String, Any?>>
                val configList = mutableListOf<Config>()
                for (map in mapList) {
                    val temp = YamlConfiguration.loadConfiguration(StringReader(""))
                    temp.createSection("a", map)
                    configList.add(EcoYamlConfigSection(temp.getConfigurationSection("a")!!, injections))
                }

                cache[path] = if (has(path)) configList else emptyList()
                getSubsections(path)
            }
        } else {
            null
        }
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