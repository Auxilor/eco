package com.willfp.eco.internal.spigot.proxy.v1_21_3

import com.willfp.eco.internal.spigot.proxy.SkullProxy
import com.willfp.eco.internal.spigot.proxy.common.modern.texture
import org.bukkit.inventory.meta.SkullMeta

class Skull : SkullProxy {
    override fun setSkullTexture(
        meta: SkullMeta,
        base64: String
    ) {
        meta.texture = base64
    }

    override fun getSkullTexture(
        meta: SkullMeta
    ): String? = meta.texture
}
