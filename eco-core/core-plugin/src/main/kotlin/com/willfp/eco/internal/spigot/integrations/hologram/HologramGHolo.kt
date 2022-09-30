package com.willfp.eco.internal.spigot.integrations.hologram

import com.willfp.eco.core.integrations.hologram.Hologram
import com.willfp.eco.core.integrations.hologram.HologramIntegration
import me.gholo.api.GHoloAPI
import org.bukkit.Location
import java.util.UUID

@Suppress("DEPRECATION")
class HologramGHolo : HologramIntegration {
    companion object {
        private val api = GHoloAPI()
    }

    override fun createHologram(location: Location, contents: MutableList<String>): Hologram {
        val id = UUID.randomUUID().toString()

        api.insertHolo(id, location, contents)

        return HologramImplGHolo(id)
    }

    override fun getPluginName(): String {
        return "GHolo"
    }

    class HologramImplGHolo(
        private val id: String
    ) : Hologram {
        override fun remove() {
            api.removeHolo(id)
        }

        override fun setContents(contents: MutableList<String>) {
            api.getHolo(id)?.content = contents
        }
    }
}
