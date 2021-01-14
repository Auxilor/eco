package com.willfp.eco.common.recipes.lookup;

import com.willfp.eco.common.recipes.parts.RecipePart;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

@SuppressWarnings("unchecked")
@UtilityClass
public final class RecipePartUtils {
    /**
     * Item lookup instance.
     */
    private static final ItemLookup itemLookup;

    /**
     * Lookup recipe part from string.
     *
     * @param key The string to test.
     * @return The generated recipe part, or null if invalid.
     */
    public RecipePart lookup(@NotNull final String key) {
        return itemLookup.lookup(key);
    }

    /**
     * Register a new lookup.
     *
     * @param key    The key of the lookup.
     * @param lookup The lookup to register, where the output is the recipe part generated.
     */
    public void registerLookup(@NotNull final String key,
                               @NotNull final Function<String, RecipePart> lookup) {
        itemLookup.registerLookup(key, lookup);
    }

    static {
        if (!Bukkit.getServicesManager().isProvidedFor(ItemLookup.class)) {
            Bukkit.getServicesManager().register(ItemLookup.class, new EcoItemLookup(), AbstractEcoPlugin.getInstance(), ServicePriority.Normal);
        }

        itemLookup = Bukkit.getServicesManager().load(ItemLookup.class);
    }
}
