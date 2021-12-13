package com.willfp.eco.internal.spigot.proxy

import org.bukkit.inventory.meta.SkullMeta

interface SkullProxy {
    fun setSkullTexture(
        meta: SkullMeta,
        base64: String
    )

    fun getSkullTexture(
        meta: SkullMeta
    ): String?
}