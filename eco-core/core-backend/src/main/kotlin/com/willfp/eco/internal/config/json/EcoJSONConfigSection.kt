package com.willfp.eco.internal.config.json

@Suppress("UNCHECKED_CAST")
class EcoJSONConfigSection(values: Map<String, Any?>) : EcoJSONConfigWrapper() {
    init {
        init(values)
    }
}