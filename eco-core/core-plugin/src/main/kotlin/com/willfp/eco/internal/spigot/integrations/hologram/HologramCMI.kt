package com.willfp.eco.internal.spigot.integrations.hologram

import com.Zrips.CMI.CMI
import com.Zrips.CMI.Modules.Holograms.CMIHologram
import com.willfp.eco.core.integrations.hologram.Hologram
import com.willfp.eco.core.integrations.hologram.HologramIntegration
import net.Zrips.CMILib.Container.CMILocation
import org.bukkit.Location
import java.util.UUID

@Suppress("DEPRECATION")
class HologramCMI : HologramIntegration {
    override fun createHologram(location: Location, contents: MutableList<String>): Hologram {
        val cmiHolo = CMIHologram(UUID.randomUUID().toString(), CMILocation(location))
        CMI.getInstance().hologramManager.addHologram(cmiHolo)

        val holo = HologramImplCMI(cmiHolo)
        holo.setContents(contents)

        cmiHolo.enable()

        return holo
    }

    override fun getPluginName(): String {
        return "CMI"
    }

    class HologramImplCMI(
        private val handle: CMIHologram
    ) : Hologram {
        override fun remove() {
            CMI.getInstance().hologramManager.removeHolo(handle)
        }

        override fun setContents(contents: MutableList<String>) {
            handle.lines = contents
        }
    }
}