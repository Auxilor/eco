package com.willfp.eco.internal.config

import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.placeholder.InjectablePlaceholder
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern

class EcoConfigSection(
    type: ConfigType,
    values: Map<String, Any?> = emptyMap(),
    injections: Map<Pattern, InjectablePlaceholder> = emptyMap()
) : EcoConfig(type) {
    init {
        this.init(values)
        this.injections = ConcurrentHashMap(injections)
    }
}
