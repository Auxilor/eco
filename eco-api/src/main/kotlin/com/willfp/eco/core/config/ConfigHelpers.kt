@file:JvmName("ConfigExtensions")

package com.willfp.eco.core.config

import com.willfp.eco.core.config.interfaces.Config
import org.bukkit.configuration.ConfigurationSection
import java.io.File
import java.io.InputStream

/** Helper class to create configs with a kotlin DSL. */
class DSLConfig internal constructor(type: ConfigType) : GenericConfig(type) {
    /**
     * Map a key to a value.
     *
     * @param value The value.
     */
    infix fun String.to(value: Any?) =
        set(this, value)

    /**
     * Helper function to create configs with a kotlin DSL.
     *
     * Inherits the config type of the sub-builder.
     *
     * @param builder The builder.
     * @return The config.
     */
    fun config(builder: DSLConfig.() -> Unit): Config =
        DSLConfig(type).apply(builder)
}

/**
 * Helper function to create configs with a kotlin DSL.
 *
 * @param builder The builder.
 * @return The config.
 */
fun config(type: ConfigType = ConfigType.YAML, builder: DSLConfig.() -> Unit): Config =
    DSLConfig(type).apply(builder)

/** @see Configs.empty */
fun emptyConfig() = Configs.empty()

/** @see Configs.empty */
fun emptyConfig(type: ConfigType) = Configs.empty(type)

/** @see Configs.fromBukkit */
fun ConfigurationSection?.toConfig() = Configs.fromBukkit(this)

/** @see Configs.fromStream */
fun InputStream?.readConfig() = Configs.fromStream(this)

/** @see Configs.fromFile */
fun File?.readConfig() = Configs.fromFile(this)

/** @see Configs.fromFile */
fun File?.readConfig(type: ConfigType) = Configs.fromFile(this, type)

/** @see Configs.fromMap */
fun Map<String?, Any?>.toConfig() = Configs.fromMap(this)

/** @see Configs.fromMap */
fun Map<String?, Any?>.toConfig(type: ConfigType) = Configs.fromMap(this, type)

/** @see Configs.fromString */
fun readConfig(contents: String, type: ConfigType) = Configs.fromString(contents, type)
