@file:Suppress("UNCHECKED_CAST")

package com.willfp.eco.internal.config

import com.google.gson.GsonBuilder
import com.willfp.eco.core.config.ConfigType
import org.bukkit.configuration.file.YamlConstructor
import org.bukkit.configuration.file.YamlRepresenter
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.LoaderOptions
import org.yaml.snakeyaml.Yaml

val ConfigType.handler: ConfigTypeHandler
    get() = if (this == ConfigType.JSON) JSONConfigTypeHandler else YamlConfigTypeHandler

abstract class ConfigTypeHandler(
    val type: ConfigType
) {
    abstract fun toMap(input: String?): Map<String, Any?>
    abstract fun toString(map: Map<String, Any?>): String
}

object YamlConfigTypeHandler : ConfigTypeHandler(ConfigType.YAML) {
    private fun newYaml(): Yaml {
        val yamlOptions = DumperOptions()
        val loaderOptions = LoaderOptions()
        val yamlRepresenter = YamlRepresenter()

        loaderOptions.maxAliasesForCollections = Int.MAX_VALUE
        yamlOptions.indent = 2
        yamlOptions.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
        yamlRepresenter.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK

        return Yaml(
            YamlConstructor(),
            yamlRepresenter,
            yamlOptions,
            loaderOptions,
        )
    }

    override fun toMap(input: String?): Map<String, Any?> {
        if (input == null || input.isBlank()) {
            return emptyMap()
        }

        return newYaml().load(input)
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

    override fun toMap(input: String?): Map<String, Any?> {
        return gson.fromJson(input ?: "{}", Map::class.java) as Map<String, Any?>
    }

    override fun toString(map: Map<String, Any?>): String {
        return gson.toJson(map)
    }
}
