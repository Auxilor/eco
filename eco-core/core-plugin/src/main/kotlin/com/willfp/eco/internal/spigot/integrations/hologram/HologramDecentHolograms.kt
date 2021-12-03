package com.willfp.eco.internal.spigot.integrations.hologram

import com.willfp.eco.core.integrations.hologram.Hologram
import com.willfp.eco.core.integrations.hologram.HologramWrapper
import eu.decentsoftware.holograms.api.DHAPI
import me.gholo.api.GHoloAPI
import org.bukkit.Location
import java.util.UUID

@Suppress("DEPRECATION")
class HologramDecentHolograms : HologramWrapper {

    override fun createHologram(location: Location, contents: MutableList<String>): Hologram {
        val id = UUID.randomUUID().toString()

        val holo = DHAPI.createHologram(id, location, contents)

        return HologramImplGHolo(id)
    }

    override fun getPluginName(): String {
        return "GHolo"
    }

    class HologramImplGHolo(
        private val id: String,
    ) : Hologram {
        override fun remove() {
            DHAPI.getHologram(id)?.destroy()
        }

        override fun setContents(contents: MutableList<String>) {
            DHAPI.setHologramLines(DHAPI.getHologram(id), contents)
        }
    }
}