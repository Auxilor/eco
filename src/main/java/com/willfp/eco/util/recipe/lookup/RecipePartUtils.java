package com.willfp.eco.util.recipe.lookup;

import com.willfp.eco.util.recipe.parts.RecipePart;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

@UtilityClass
public final class RecipePartUtils {
    /**
     * Instance of registered lookups.
     */
    private final ItemLookup lookup;

    /**
     * Register a new lookup.
     *
     * @param key            The key of the lookup.
     * @param lookupFunction The lookup to register, where the output is the recipe part generated.
     */
    public void registerLookup(@NotNull final String key,
                               @NotNull final Function<String, RecipePart> lookupFunction) {
        lookup.registerLookup(key, lookupFunction);
    }

    /**
     * Lookup recipe part from string.
     *
     * @param key The string to test.
     * @return The generated recipe part, or null if invalid.
     */
    public RecipePart lookup(@NotNull final String key) {
        return lookup.lookup(key);
    }

    static {
        lookup = Bukkit.getServicesManager().load(ItemLookup.class);
    }
}
