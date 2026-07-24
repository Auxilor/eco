package com.willfp.eco.internal.spigot.proxy.v1_21_10

import com.willfp.eco.core.integrations.hologram.HologramOptions
import com.willfp.eco.internal.spigot.proxies.HologramProxy
import com.willfp.eco.internal.spigot.proxies.NativeHologramHandle
import com.willfp.eco.internal.spigot.proxy.v1_21_10.hologram.V1_21_10HologramHandle
import org.bukkit.Location

class Hologram : HologramProxy {
    override fun createHandle(location: Location, options: HologramOptions): NativeHologramHandle {
        return V1_21_10HologramHandle.create(location, options)
    }
}
