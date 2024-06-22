package com.willfp.eco.internal.spigot.proxy.common

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
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

        val profile = profile[this] as GameProfile? ?: return null
        val properties = profile.properties ?: return null
        val props = properties["textures"] ?: return null
        val prop = props.toMutableList().firstOrNull() ?: return null
        return value[prop] as String?
    }
    set(base64) {
        if (!::setProfile.isInitialized) {
            // Same here; that's why I can't delegate to a lazy initializer
            setProfile = this.javaClass.getDeclaredMethod("setProfile", GameProfile::class.java)
            setProfile.isAccessible = true
        }

        /* This length check below was lost in the conversion. For some reason the base64
        * string is length 8 when this is called pretty frequently, causing an
        * out of bounds exception.
        *
        * Could not pass event EntityPotionEffectEvent to Talismans v5.116.0
        * java.lang.StringIndexOutOfBoundsException: begin -12, end 8, length 8
        * at java.lang.String.checkBoundsBeginEnd(String.java:4604) ~[?:?]
        * at java.lang.String.substring(String.java:2707) ~[?:?]
        * at java.lang.String.substring(String.java:2680) ~[?:?]
        * at com.willfp.eco.internal.spigot.proxy.v1_19_R1.common.SkullKt.setTexture(Skull.kt:36)
        *
        if (base64.length < 20) {
            return
        }
        *
        * ^ Update to this comment: a length 8 string ("textures") was being sent
        * because the get() method wasn't working right. This has been fixed, but the
        * check needs to remain implemented.
        */

        if (base64 == null || base64.length < 20) {
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
