package com.willfp.eco.internal.config

import org.yaml.snakeyaml.nodes.Node
import org.yaml.snakeyaml.representer.Represent
import org.yaml.snakeyaml.representer.Representer

class AnchorlessRepresenter : Representer() {
    init {
        for ((clazz, existing) in multiRepresenters.entries.toSet()) {
            multiRepresenters[clazz] = NoAnchorRepresentDelegate(existing)
        }
    }

    private inner class NoAnchorRepresentDelegate(
        val handle: Represent
    ) : Represent {
        override fun representData(data: Any?): Node {
            val representedObjectsOnEntry = representedObjects.toMap()
            return try {
                handle.representData(data)
            } finally {
                representedObjects.clear()
                representedObjects.putAll(representedObjectsOnEntry)
            }
        }
    }
}
