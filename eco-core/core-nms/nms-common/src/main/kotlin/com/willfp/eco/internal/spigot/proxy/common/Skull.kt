package com.willfp.eco.internal.spigot.proxy.common

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import org.bukkit.inventory.meta.SkullMeta
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.UUID

private lateinit var setProfile: Method
private lateinit var profile: Field

var SkullMeta.texture: String?
    get() {
        if (!::profile.isInitialized) {
            // Assumes instance of CraftMetaSkull; package-private class so can't do manual type check
            profile = this.javaClass.getDeclaredField("profile")
            profile.isAccessible = true
        }
        val profile = profile[this] as GameProfile? ?: return null
        val properties = profile.properties ?: return null
        val prop = properties["textures"] ?: return null
        return prop.toMutableList().firstOrNull()?.value
    }
    set(base64) {
        if (!::setProfile.isInitialized) {
            // Same here; that's why I can't delegate to a lazy initializer
            setProfile = this.javaClass.getDeclaredMethod("setProfile", GameProfile::class.java)
            setProfile.isAccessible = true
        }

        if (base64 == null) {
            setProfile.invoke(this, null)
        } else {
            val uuid = UUID(
                base64.substring(base64.length - 20).hashCode().toLong(),
                base64.substring(base64.length - 10).hashCode().toLong()
            )
            val profile = GameProfile(uuid, "eco")
            profile.properties.put("textures", Property("textures", base64))
            setProfile.invoke(this, profile)
        }
    }
