package com.willfp.eco.internal.config

import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.placeholder.InjectablePlaceholder

class EcoConfigSection(
    type: ConfigType,
    values: Map<String, Any?> = emptyMap(),
    injections: Map<String, InjectablePlaceholder> = emptyMap()
) : EcoConfig(type) {
    init {
        this.init(values, injections)
    }
}
