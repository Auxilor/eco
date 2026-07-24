package com.willfp.eco.internal.spigot.proxy.v26_2

import com.willfp.eco.core.integrations.hologram.HologramOptions
import com.willfp.eco.internal.spigot.proxies.HologramProxy
import com.willfp.eco.internal.spigot.proxies.NativeHologramHandle
import com.willfp.eco.internal.spigot.proxy.v26_2.hologram.V26_2HologramHandle
import org.bukkit.Location

class Hologram : HologramProxy {
    override fun createHandle(location: Location, options: HologramOptions): NativeHologramHandle {
        return V26_2HologramHandle.create(location, options)
    }
}
