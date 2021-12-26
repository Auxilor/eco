package com.willfp.eco.internal.spigot.proxy.v1_18_R1

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import com.willfp.eco.internal.spigot.proxy.SkullProxy
import org.bukkit.inventory.meta.SkullMeta
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.UUID

class Skull : SkullProxy {
    private lateinit var setProfile: Method
    private lateinit var profile: Field

    override fun setSkullTexture(
        meta: SkullMeta,
        base64: String
    ) {
        if (!this::setProfile.isInitialized) {
            setProfile = meta.javaClass.getDeclaredMethod("setProfile", GameProfile::class.java)
            setProfile.isAccessible = true
        }
        val uuid = UUID(
            base64.substring(base64.length - 20).hashCode().toLong(),
            base64.substring(base64.length - 10).hashCode().toLong()
        )
        val profile = GameProfile(uuid, "eco")
        profile.properties.put("textures", Property("textures", base64))
        setProfile.invoke(meta, profile)
    }

    override fun getSkullTexture(
        meta: SkullMeta
    ): String? {
        if (!this::profile.isInitialized) {
            profile = meta.javaClass.getDeclaredField("profile")
            profile.isAccessible = true
        }
        val profile = profile[meta] as GameProfile? ?: return null
        val properties = profile.properties ?: return null
        val prop = properties["textures"] ?: return null
        return prop.toMutableList().firstOrNull()?.name
    }
}