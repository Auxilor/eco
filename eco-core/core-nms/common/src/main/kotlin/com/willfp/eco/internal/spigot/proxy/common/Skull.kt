package com.willfp.eco.internal.spigot.proxy.common

import com.destroystokyo.paper.profile.ProfileProperty
import com.willfp.eco.core.Prerequisite
import io.papermc.paper.datacomponent.item.ResolvableProfile
import org.bukkit.inventory.meta.SkullMeta

var SkullMeta.texture: String?
    get() {
        if (!Prerequisite.HAS_PAPER.isMet) {
            return null
        }

        val profile = this.playerProfile ?: return null
        val property = profile.properties.firstOrNull { it.name == "textures" } ?: return null

        return property.value
    }
    set(base64) {
        if (!Prerequisite.HAS_PAPER.isMet) {
            return
        }

        if (base64 == null) {
            this.playerProfile = null
            return
        } else {
            val profile = ResolvableProfile.resolvableProfile()
                .addProperty(ProfileProperty("textures", base64))
                .build()
                .resolve().get()
            this.playerProfile = profile
        }
    }
