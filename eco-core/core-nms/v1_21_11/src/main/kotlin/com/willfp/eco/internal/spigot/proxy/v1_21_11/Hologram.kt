package com.willfp.eco.internal.spigot.proxy.v1_21_11

import com.willfp.eco.core.integrations.hologram.HologramOptions
import com.willfp.eco.internal.spigot.proxies.HologramProxy
import com.willfp.eco.internal.spigot.proxies.NativeHologramHandle
import com.willfp.eco.internal.spigot.proxy.v1_21_11.hologram.V1_21_11HologramHandle
import org.bukkit.Location

class Hologram : HologramProxy {
    override fun createHandle(location: Location, options: HologramOptions): NativeHologramHandle {
        return V1_21_11HologramHandle.create(location, options)
    }
}
