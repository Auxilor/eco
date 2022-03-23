package com.willfp.eco.internal.config

import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.placeholder.StaticPlaceholder

@Suppress("UNCHECKED_CAST")
class EcoConfigSection(
    type: ConfigType,
    values: Map<String, Any?>,
    injections: Collection<StaticPlaceholder> = emptyList()
) : EcoConfig(type) {
    init {
        this.init(values)
        this.injections = injections.toMutableList()
    }
}
