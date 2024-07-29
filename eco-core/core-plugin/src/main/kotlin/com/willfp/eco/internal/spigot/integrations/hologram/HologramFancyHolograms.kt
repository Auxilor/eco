package com.willfp.eco.internal.spigot.integrations.hologram

import com.willfp.eco.core.integrations.hologram.Hologram
import com.willfp.eco.core.integrations.hologram.HologramIntegration
import de.oliver.fancyholograms.api.FancyHologramsPlugin
import de.oliver.fancyholograms.api.data.TextHologramData
import org.bukkit.Location
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

class HologramFancyHolograms : HologramIntegration {
    private val manager
        get() = FancyHologramsPlugin.get().hologramManager

    override fun createHologram(location: Location, contents: List<String>): Hologram {
        val id = UUID.randomUUID().toString()

        val data = TextHologramData(id, location)
        data.text = contents
        data.isPersistent = false

        val holo = manager.create(data)
        manager.addHologram(holo)

        return HologramImplFancyHolograms(id)
    }

    override fun getPluginName(): String {
        return "FancyHolograms"
    }

    inner class HologramImplFancyHolograms(
        private val id: String,
    ) : Hologram {
        override fun remove() {
            val hologram = manager.getHologram(id).getOrNull() ?: return
            manager.removeHologram(hologram)
        }

        override fun setContents(contents: List<String>) {
            val hologram = manager.getHologram(id).getOrNull() ?: return
            val data = hologram.data as? TextHologramData ?: return
            data.text = contents
        }
    }
}
