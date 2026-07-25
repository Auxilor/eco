package com.willfp.eco.internal

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.PluginProps
import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.config.Configs
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.version.Version
import java.io.InputStream
import java.io.InputStreamReader
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.nodes.MappingNode
import org.yaml.snakeyaml.nodes.Node
import org.yaml.snakeyaml.nodes.ScalarNode
import org.yaml.snakeyaml.nodes.SequenceNode

object EcoPropsParser : PluginProps.PropsParser<Config> {
    override fun parseFrom(config: Config): PluginProps {
        val props = blankProps

        if (!config.has("options")) {
            parseAndApplyOptions(props, config)
        } else {
            parseAndApplyOptions(props, config.getSubsection("options"))
        }

        return props.apply {
            for (envConfig in config.getSubsections("environment")) {
                val name = envConfig.getStringOrNull("name") ?: continue
                val value = envConfig.getStringOrNull("value") ?: continue

                this.setEnvironmentVariable(name, value)
            }
        }
    }

    private fun parseAndApplyOptions(props: PluginProps, config: Config) {
        val resourceId = config.getIntOrNull("resource-id") ?: 0
        val bStatsId = config.getIntOrNull("bstats-id") ?: 0
        val proxyPackage = config.getStringOrNull("proxy-package") ?: ""
        val color = config.getStringOrNull("color") ?: "&f"
        val supportsExtensions = config.getBoolOrNull("supports-extensions") ?: false
        val ecoApiVersion = config.getStringOrNull("eco-api-version")?.let { Version(it) }

        props.apply {
            this.resourceId = resourceId
            this.bStatsId = bStatsId
            this.proxyPackage = proxyPackage
            this.color = color
            this.isSupportingExtensions = supportsExtensions

            if (ecoApiVersion != null) {
                this.ecoApiVersion = ecoApiVersion
            }
        }
    }

    fun parseForPlugin(plugin: Class<out EcoPlugin>): PluginProps {
        if (!PluginProps.hasParserFor(Config::class.java)) {
            PluginProps.registerParser(Config::class.java, this)
        }

        return PluginProps.parse(
            readEcoConfig(plugin.getResourceAsStream("/eco.yml")),
            Config::class.java
        )
    }
}

/**
 * eco.yml is read through raw YAML nodes instead of the generic config pipeline
 * (Bukkit's YamlConfiguration), so scalar values are taken as the exact literal
 * text authored, rather than being silently coerced into a lossy Double by
 * YAML's implicit float resolver (eg. "libreforge version": 2026.30 becoming
 * 2026.3, dropping the trailing zero).
 */
internal fun readEcoConfig(stream: InputStream?): Config {
    val root = stream?.use {
        Yaml().compose(InputStreamReader(it, Charsets.UTF_8)) as? MappingNode
    } ?: return Configs.empty(ConfigType.YAML)

    val optionsNode = root.child("options") as? MappingNode ?: root

    val options = mutableMapOf<String, Any>()
    optionsNode.child("resource-id").scalarOrNull()?.toIntOrNull()?.let { options["resource-id"] = it }
    optionsNode.child("bstats-id").scalarOrNull()?.toIntOrNull()?.let { options["bstats-id"] = it }
    optionsNode.child("color").scalarOrNull()?.let { options["color"] = it }
    optionsNode.child("proxy-package").scalarOrNull()?.let { options["proxy-package"] = it }
    optionsNode.child("eco-api-version").scalarOrNull()?.let { options["eco-api-version"] = it }
    optionsNode.child("supports-extensions").scalarOrNull()?.let {
        options["supports-extensions"] = it.equals("true", ignoreCase = true)
    }

    val environment = (root.child("environment") as? SequenceNode)?.value.orEmpty().mapNotNull { entryNode ->
        val entry = entryNode as? MappingNode ?: return@mapNotNull null
        val name = entry.child("name").scalarOrNull() ?: return@mapNotNull null
        val value = entry.child("value").scalarOrNull() ?: return@mapNotNull null
        mapOf("name" to name, "value" to value)
    }

    return Configs.fromMap(
        mapOf(
            "options" to options,
            "environment" to environment
        ),
        ConfigType.YAML
    )
}

private fun Node?.scalarOrNull(): String? =
    (this as? ScalarNode)?.value

private fun MappingNode.child(key: String): Node? =
    value.firstOrNull { (it.keyNode as? ScalarNode)?.value == key }?.valueNode
