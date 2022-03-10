package com.willfp.eco.internal.config.json

import com.willfp.eco.core.placeholder.StaticPlaceholder

@Suppress("UNCHECKED_CAST")
class EcoJSONConfigSection(values: Map<String, Any?>, injections: Collection<StaticPlaceholder> = emptyList()) : EcoJSONConfigWrapper() {
    init {
        init(values)
        this.injections = injections.toMutableList()
    }
}