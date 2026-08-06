@file:JvmName("SkullUtilsExtensions")

package com.willfp.eco.util

import org.bukkit.inventory.meta.SkullMeta

/**
 * The base64-encoded texture of this skull meta.
 *
 * Null when the skull has no texture. Assigning null is a no-op: it does not clear an existing
 * texture, so the value read back may differ from the value assigned.
 *
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
