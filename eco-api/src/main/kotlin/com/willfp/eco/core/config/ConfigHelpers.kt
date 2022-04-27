@file:JvmName("ConfigExtensions")

package com.willfp.eco.core.config

import com.willfp.eco.core.config.interfaces.Config

/**
 * Helper class to create configs with a kotlin DSL.
 */
class DSLConfig internal constructor(type: ConfigType) : TransientConfig(emptyMap(), type) {
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
