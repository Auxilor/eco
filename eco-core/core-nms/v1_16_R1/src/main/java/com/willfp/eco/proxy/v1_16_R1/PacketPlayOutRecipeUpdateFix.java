package com.willfp.eco.proxy.v1_16_R1;

import com.willfp.eco.proxy.proxies.PacketPlayOutRecipeUpdateFixProxy;
import net.minecraft.server.v1_16_R1.IRecipe;
import net.minecraft.server.v1_16_R1.PacketPlayOutRecipeUpdate;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("unchecked")
public final class PacketPlayOutRecipeUpdateFix implements PacketPlayOutRecipeUpdateFixProxy {
    @Override
    public List<Object> splitPackets(@NotNull final Object object,
                                     @NotNull final Player player) {
        if (!(object instanceof PacketPlayOutRecipeUpdate)) {
            throw new IllegalArgumentException("Parameter not packet!");
        }

        PacketPlayOutRecipeUpdate oldPacket = (PacketPlayOutRecipeUpdate) object;
        List<IRecipe<?>> recipes = new ArrayList<>();
        try {
            Field f = oldPacket.getClass().getDeclaredField("a");
            f.setAccessible(true);
            recipes.addAll((Collection<? extends IRecipe<?>>) f.get(oldPacket));
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        List<Object> splitPackets = new ArrayList<>();
        List<IRecipe<?>> splitRecipes = new ArrayList<>();
        for (int i = 0; i < recipes.size(); i++) {
            splitRecipes.add(recipes.get(i));
            if (i % 100 == 0) {
                PacketPlayOutRecipeUpdate newPacket = new PacketPlayOutRecipeUpdate(splitRecipes);
                splitPackets.add(newPacket);
                splitRecipes.clear();
            }
        }

        return splitPackets;
    }
}
