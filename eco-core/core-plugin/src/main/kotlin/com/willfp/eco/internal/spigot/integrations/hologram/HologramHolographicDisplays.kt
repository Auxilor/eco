package com.willfp.eco.internal.spigot.integrations.hologram

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.integrations.hologram.Hologram
import com.willfp.eco.core.integrations.hologram.HologramIntegration
import org.bukkit.Location

class HologramHolographicDisplays(
    private val plugin: EcoPlugin
) : HologramIntegration {

    override fun createHologram(location: Location, contents: MutableList<String>): Hologram {
        val hologram = HologramImplHolographicDisplays(
            HologramsAPI.createHologram(plugin, location)
        )

        hologram.setContents(contents)

        return hologram
    }

    override fun getPluginName(): String {
        return "HolographicDisplays"
    }

    class HologramImplHolographicDisplays(
        private val handle: com.gmail.filoghost.holographicdisplays.api.Hologram
    ) : Hologram {
        override fun remove() {
            handle.delete()
        }

        override fun setContents(contents: MutableList<String>) {
            handle.clearLines()
            for (line in contents) {
                handle.appendTextLine(line)
            }
        }
    }
}