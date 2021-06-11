package com.willfp.eco.proxy.v1_17_R1;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.willfp.eco.proxy.proxies.SkullProxy;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public final class Skull implements SkullProxy {
    /**
     * Cached method to set the gameProfile.
     */
    private Method setProfile = null;

    @Override
    public void setSkullTexture(@NotNull final SkullMeta meta,
                                @NotNull final String base64) {
        try {
            if (setProfile == null) {
                setProfile = meta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
                setProfile.setAccessible(true);
            }

            UUID uuid = new UUID(
                    base64.substring(base64.length() - 20).hashCode(),
                    base64.substring(base64.length() - 10).hashCode()
            );

            GameProfile profile = new GameProfile(uuid, "talismans");
            profile.getProperties().put("textures", new Property("textures", base64));

            setProfile.invoke(meta, profile);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
