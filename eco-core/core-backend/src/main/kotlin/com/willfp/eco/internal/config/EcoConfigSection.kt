package com.willfp.eco.internal.config

import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.placeholder.InjectablePlaceholder
import java.util.concurrent.CopyOnWriteArrayList

class EcoConfigSection(
    type: ConfigType,
    values: Map<String, Any?> = emptyMap(),
    injections: List<InjectablePlaceholder> = emptyList()
) : EcoConfig(type) {
    init {
        this.init(values)
        this.injections = CopyOnWriteArrayList(injections)
    }
}
