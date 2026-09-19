package com.willfp.eco.internal.spigot.proxy.v26_3

import com.willfp.eco.core.integrations.hologram.HologramOptions
import com.willfp.eco.internal.spigot.proxies.HologramProxy
import com.willfp.eco.internal.spigot.proxies.NativeHologramHandle
import com.willfp.eco.internal.spigot.proxy.v26_3.hologram.V26_3HologramHandle
import org.bukkit.Location

class Hologram : HologramProxy {
    override fun createHandle(location: Location, options: HologramOptions): NativeHologramHandle {
        return V26_3HologramHandle.create(location, options)
    }
}
