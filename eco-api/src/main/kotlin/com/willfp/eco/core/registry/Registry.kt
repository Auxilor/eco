@file:JvmName("RegistryExtensions")

package com.willfp.eco.core.registry

/** @see Registry.tryFitPattern */
fun String.tryFitRegistryPattern(): String =
    Registry.tryFitPattern(this)
