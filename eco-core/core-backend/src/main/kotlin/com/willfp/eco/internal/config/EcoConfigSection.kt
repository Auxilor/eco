package com.willfp.eco.internal.config

import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.placeholder.InjectablePlaceholder

class EcoConfigSection(
    type: ConfigType,
    values: Map<String, Any?> = emptyMap(),
    injections: Collection<InjectablePlaceholder> = emptyList()
) : EcoConfig(type) {
    init {
        this.init(values)
        this.injections = injections.toMutableList()
    }
}
