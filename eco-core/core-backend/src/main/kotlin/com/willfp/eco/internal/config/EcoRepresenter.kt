package com.willfp.eco.internal.config

import com.willfp.eco.core.config.interfaces.Config
import org.yaml.snakeyaml.nodes.Node
import org.yaml.snakeyaml.representer.Represent
import org.yaml.snakeyaml.representer.Representer

@Suppress("DEPRECATION")
class EcoRepresenter : Representer() {
    init {
        multiRepresenters[Config::class.java] = RepresentConfig(multiRepresenters[Map::class.java]!!)
    }

    private class RepresentConfig(
        val handle: Represent
    ) : Represent {
        override fun representData(data: Any): Node {
            data as Config
            return handle.representData(data.toMap())
        }
    }
}
