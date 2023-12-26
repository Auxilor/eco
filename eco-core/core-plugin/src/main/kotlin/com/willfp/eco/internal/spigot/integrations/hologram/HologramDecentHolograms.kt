package com.willfp.eco.internal.spigot.integrations.hologram

import com.willfp.eco.core.integrations.hologram.Hologram
import com.willfp.eco.core.integrations.hologram.HologramIntegration
import eu.decentsoftware.holograms.api.DHAPI
import org.bukkit.Location
import java.util.UUID

class HologramDecentHolograms : HologramIntegration {
    override fun createHologram(location: Location, contents: List<String>): Hologram {
        val id = UUID.randomUUID().toString()

        DHAPI.createHologram(id, location, contents)

        return HologramImplDecentHolograms(id)
    }

    override fun getPluginName(): String {
        return "DecentHolograms"
    }

    class HologramImplDecentHolograms(
        private val id: String,
    ) : Hologram {
        override fun remove() {
            DHAPI.getHologram(id)?.destroy()
        }

        override fun setContents(contents: List<String>) {
            DHAPI.setHologramLines(DHAPI.getHologram(id), contents)
        }
    }
}
