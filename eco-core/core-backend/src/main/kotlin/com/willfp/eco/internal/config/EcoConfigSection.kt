package com.willfp.eco.internal.config

import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.placeholder.InjectablePlaceholder
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern

class EcoConfigSection(
    type: ConfigType,
    values: Map<String, Any?> = emptyMap(),
    injections: MutableMap<Pattern, InjectablePlaceholder> = mutableMapOf()
) : EcoConfig(type) {
    init {
        this.init(values)
        this.injections = ConcurrentHashMap(injections)
    }
}
