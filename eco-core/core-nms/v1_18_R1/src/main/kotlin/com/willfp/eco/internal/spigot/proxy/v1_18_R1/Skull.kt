package com.willfp.eco.internal.spigot.proxy.v1_18_R1

import com.willfp.eco.internal.spigot.proxy.SkullProxy
import org.bukkit.inventory.meta.SkullMeta

class Skull : SkullProxy {
    override fun setSkullTexture(
        meta: SkullMeta,
        base64: String
    ) {
        // Do nothing
    }

    override fun getSkullTexture(
        meta: SkullMeta
    ): String? {
        return null
    }
}
