package com.willfp.eco.internal.spigot.proxy.common.modern

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import net.minecraft.world.item.component.ResolvableProfile
import org.bukkit.inventory.meta.SkullMeta
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.UUID

private lateinit var setProfile: Method
private lateinit var profile: Field
private lateinit var value: Field

var SkullMeta.texture: String?
    get() {
        if (!::value.isInitialized) {
            // Doing it this way because Property was changed to be a record and this is
            // a quick hack to get around that
            value = Property::class.java.getDeclaredField("value")
            value.isAccessible = true
        }

        if (!::profile.isInitialized) {
            // Assumes instance of CraftMetaSkull; package-private class so can't do manual type check
            profile = this.javaClass.getDeclaredField("profile")
            profile.isAccessible = true
        }

        val profile = profile[this] as ResolvableProfile? ?: return null
        val properties = profile.properties ?: return null
        val props = properties["textures"] ?: return null
        val prop = props.toMutableList().firstOrNull() ?: return null
        return value[prop] as String?
    }
    set(base64) {
        if (!::setProfile.isInitialized) {
            // Same here; that's why I can't delegate to a lazy initializer
            setProfile = this.javaClass.getDeclaredMethod("setProfile", ResolvableProfile::class.java)
            setProfile.isAccessible = true
        }

        if (base64 == null || base64.length < 20) {
            setProfile.invoke(this, null)
        } else {
            val uuid = UUID(
                base64.substring(base64.length - 20).hashCode().toLong(),
                base64.substring(base64.length - 10).hashCode().toLong()
            )
            val profile = GameProfile(uuid, "eco")
            profile.properties.put("textures", Property("textures", base64))
            val resolvable = ResolvableProfile(profile)
            setProfile.invoke(this, resolvable)
        }
    }
