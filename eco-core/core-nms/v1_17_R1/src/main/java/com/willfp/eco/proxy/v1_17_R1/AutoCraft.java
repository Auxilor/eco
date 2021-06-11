package com.willfp.eco.proxy.v1_17_R1;

import com.willfp.eco.proxy.proxies.AutoCraftProxy;
import net.minecraft.network.protocol.game.PacketPlayOutAutoRecipe;
import net.minecraft.resources.MinecraftKey;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public final class AutoCraft implements AutoCraftProxy {
    @Override
    public void modifyPacket(@NotNull final Object packet) throws NoSuchFieldException, IllegalAccessException {
        PacketPlayOutAutoRecipe recipePacket = (PacketPlayOutAutoRecipe) packet;
        Field fKey = recipePacket.getClass().getDeclaredField("b");
        fKey.setAccessible(true);
        MinecraftKey key = (MinecraftKey) fKey.get(recipePacket);
        fKey.set(recipePacket, new MinecraftKey(key.getNamespace(), key.getKey() + "_displayed"));
    }
}
