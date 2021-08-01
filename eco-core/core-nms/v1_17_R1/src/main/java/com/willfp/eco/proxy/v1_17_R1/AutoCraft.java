package com.willfp.eco.proxy.v1_17_R1;

import com.willfp.eco.proxy.AutoCraftProxy;
import net.minecraft.network.protocol.game.ClientboundPlaceGhostRecipePacket;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public final class AutoCraft implements AutoCraftProxy {
    @Override
    public void modifyPacket(@NotNull final Object packet) throws NoSuchFieldException, IllegalAccessException {
        ClientboundPlaceGhostRecipePacket recipePacket = (ClientboundPlaceGhostRecipePacket) packet;
        Field fKey = recipePacket.getClass().getDeclaredField("b");
        fKey.setAccessible(true);
        ResourceLocation key = (ResourceLocation) fKey.get(recipePacket);
        fKey.set(recipePacket, new ResourceLocation(key.getNamespace(), key.getPath() + "_displayed"));
    }
}
