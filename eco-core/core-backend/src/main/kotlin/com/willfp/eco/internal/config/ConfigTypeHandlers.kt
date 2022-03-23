@file:Suppress("UNCHECKED_CAST")

package com.willfp.eco.internal.config

import com.google.gson.GsonBuilder
import com.willfp.eco.core.config.ConfigType
import org.bukkit.configuration.file.YamlConstructor
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.LoaderOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.representer.Representer

val ConfigType.handler: ConfigTypeHandler
    get() = if (this == ConfigType.JSON) JSONConfigTypeHandler else YamlConfigTypeHandler

abstract class ConfigTypeHandler(
    val type: ConfigType
) {
    fun toMap(input: String?): Map<String, Any?> {
        if (input == null || input.isBlank()) {
            return emptyMap()
        }

        return ensureMapTypes(parseToMap(input))
    }

    private fun ensureMapTypes(map: Map<*, *>): Map<String, Any?> {
        val building = mutableMapOf<String, Any?>()

        for (entry in map.entries) {
            val value = entry.value?.let {
                if (it is Map<*, *>) {
                    ensureMapTypes(it.toMutableMap())
                } else {
                    it
                }
            }

            if (entry.key == null) {
                continue
            }

            building[entry.key.toString()] = value
        }

        return building
    }

    protected abstract fun parseToMap(input: String): Map<*, *>

    abstract fun toString(map: Map<String, Any?>): String
}

object YamlConfigTypeHandler : ConfigTypeHandler(ConfigType.YAML) {
    private fun newYaml(): Yaml {
        val yamlOptions = DumperOptions()
        val loaderOptions = LoaderOptions()
        val representer = Representer()

        loaderOptions.maxAliasesForCollections = Int.MAX_VALUE
        yamlOptions.indent = 2
        yamlOptions.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
        representer.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK

        return Yaml(
            YamlConstructor(),
            representer,
            yamlOptions,
            loaderOptions,
        )
    }

    override fun parseToMap(input: String): Map<*, *> {
        return newYaml().load(input) ?: emptyMap<Any, Any>()
    }

    override fun toString(map: Map<String, Any?>): String {
        return newYaml().dump(map)
    }
}

object JSONConfigTypeHandler : ConfigTypeHandler(ConfigType.JSON) {
    private val gson = GsonBuilder()
        .setPrettyPrinting()
        .disableHtmlEscaping()
        .create()

    override fun parseToMap(input: String): Map<*, *> {
        return gson.fromJson(input, Map::class.java)
    }

    override fun toString(map: Map<String, Any?>): String {
        return gson.toJson(map)
    }
}
