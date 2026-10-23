package com.willfp.eco.internal.spigot.proxy.common

import com.destroystokyo.paper.profile.ProfileProperty
import com.google.gson.JsonParser
import com.willfp.eco.core.Prerequisite
import io.papermc.paper.datacomponent.item.ResolvableProfile
import java.net.URI
import java.util.Base64
import java.util.UUID
import org.bukkit.Bukkit
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.profile.PlayerProfile

var SkullMeta.texture: String?
    get() {
        if (!Prerequisite.HAS_PAPER.isMet) {
            val url = this.ownerProfile?.run { textures.skin } ?: return null
            return Base64.getEncoder().encodeToString("""{"textures":{"SKIN":{"url":"$url"}}}""".toByteArray())
        }

        val profile = this.playerProfile ?: return null
        val property = profile.properties.firstOrNull { it.name == "textures" } ?: return null

        return property.value
    }
    set(base64) {
        if (!Prerequisite.HAS_PAPER.isMet) {
            this.ownerProfile = base64?.let { createSpigotProfile(it) }
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

private fun createSpigotProfile(base64: String): PlayerProfile? {
    val url = runCatching {
        JsonParser.parseString(String(Base64.getDecoder().decode(base64))).asJsonObject
            .getAsJsonObject("textures")
            .getAsJsonObject("SKIN")
            .get("url")
            .asString
    }.getOrNull() ?: return null

    return Bukkit.createPlayerProfile(UUID.randomUUID()).apply {
        textures.skin = URI(url).toURL()
    }
}
