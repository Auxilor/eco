@file:JvmName("ConfigExtensions")

package com.willfp.eco.core.config

import com.willfp.eco.core.config.interfaces.Config

/**
 * Helper class to create configs with a kotlin DSL.
 */
class DSLConfig internal constructor() : BuildableConfig() {
    infix fun String.to(value: Any?) =
        set(this, value)
}

/**
 * Helper function to create configs with a kotlin DSL.
 *
 * @param builder The builder.
 * @return The config.
 */
fun config(builder: DSLConfig.() -> Unit): Config =
    DSLConfig().apply(builder)
