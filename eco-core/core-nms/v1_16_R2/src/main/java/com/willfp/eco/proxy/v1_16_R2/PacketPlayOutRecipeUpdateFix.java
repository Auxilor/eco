package com.willfp.eco.proxy.v1_16_R2;

import com.willfp.eco.proxy.proxies.PacketPlayOutRecipeUpdateFixProxy;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import net.minecraft.server.v1_16_R2.IRecipe;
import net.minecraft.server.v1_16_R2.PacketPlayOutRecipeUpdate;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("unchecked")
public final class PacketPlayOutRecipeUpdateFix implements PacketPlayOutRecipeUpdateFixProxy {
    @Override
    public Object splitAndModifyPacket(@NotNull final Object object) {
        if (!(object instanceof PacketPlayOutRecipeUpdate)) {
            throw new IllegalArgumentException("Parameter not packet!");
        }

        PacketPlayOutRecipeUpdate oldPacket = (PacketPlayOutRecipeUpdate) object;
        List<IRecipe<?>> recipes = new ArrayList<>();
        Field f = null;
        try {
            f = oldPacket.getClass().getDeclaredField("a");
            f.setAccessible(true);
            recipes.addAll((Collection<? extends IRecipe<?>>) f.get(oldPacket));
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        if (f == null) {
            return null;
        }

        List<IRecipe<?>> externRecipes = new ArrayList<>();
        for (IRecipe<?> recipe : new ArrayList<>(recipes)) {
            if (AbstractEcoPlugin.LOADED_ECO_PLUGINS.contains(recipe.getKey().getNamespace())) {
                externRecipes.add(recipe);
                recipes.remove(recipe);
            }
        }

        if (externRecipes.isEmpty()) {
            return null;
        }

        try {
            f.set(object, recipes);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return new PacketPlayOutRecipeUpdate(externRecipes);
    }
}
