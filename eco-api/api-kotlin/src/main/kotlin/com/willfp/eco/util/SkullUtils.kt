@file:JvmName("SkullUtilsExtensions")

package com.willfp.eco.util

import org.bukkit.inventory.meta.SkullMeta

/**
 * @see SkullUtils.getSkullTexture
 * @see SkullUtils.setSkullTexture
 */
var SkullMeta.texture: String?
    get() = SkullUtils.getSkullTexture(this)
    set(value) {
        if (value != null) {
            SkullUtils.setSkullTexture(this, value)
        }
    }
