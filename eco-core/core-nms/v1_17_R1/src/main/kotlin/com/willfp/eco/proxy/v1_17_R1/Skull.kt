package com.willfp.eco.proxy.v1_17_R1

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import org.bukkit.inventory.meta.SkullMeta
import proxy.SkullProxy
import java.lang.reflect.Method
import java.util.*

class Skull : SkullProxy {
    private lateinit var setProfile: Method

    override fun setSkullTexture(
        meta: SkullMeta,
        base64: String
    ) {
        try {
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
        } catch (e: ReflectiveOperationException) {
            e.printStackTrace()
        }
    }
}