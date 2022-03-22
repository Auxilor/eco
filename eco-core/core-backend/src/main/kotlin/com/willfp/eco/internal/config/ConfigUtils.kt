package com.willfp.eco.internal.config

import com.willfp.eco.core.config.interfaces.Config

fun Any.ensureConfigSerializable(): Any = when (this) {
    is Config -> this.toMap()
    is Collection<*> -> {
        val first = this.firstOrNull()
        if (first is Config) {
            @Suppress("UNCHECKED_CAST")
            this as Collection<Config>
            this.map { it.toMap() }.toMutableList()
        } else if (this.isEmpty()) {
            mutableListOf() // Don't use EmptyList, causes anchors as they have the same reference
        } else {
            this.toMutableList()
        }
    }
    else -> this
}
