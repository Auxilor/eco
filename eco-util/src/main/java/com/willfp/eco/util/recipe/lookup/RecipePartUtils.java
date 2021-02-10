package com.willfp.eco.util.recipe.lookup;

import com.willfp.eco.util.recipe.parts.EmptyRecipePart;
import com.willfp.eco.util.recipe.parts.RecipePart;
import com.willfp.eco.util.recipe.parts.SimpleRecipePart;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@UtilityClass
public final class RecipePartUtils {
    /**
     * All recipes.
     */
    private static final Map<String, Function<String, RecipePart>> TESTS = new HashMap<>();

    /**
     * Register a new lookup.
     *
     * @param key            The key of the lookup.
     * @param lookupFunction The lookup to register, where the output is the recipe part generated.
     */
    public void registerLookup(@NotNull final String key,
                               @NotNull final Function<String, RecipePart> lookupFunction) {
        TESTS.put(key, lookupFunction);
    }

    /**
     * Lookup recipe part from string.
     *
     * @param key The string to test.
     * @return The generated recipe part, or null if invalid.
     */
    public RecipePart lookup(@NotNull final String key) {
        Function<String, RecipePart> lookup = TESTS.get(key);

        if (lookup == null) {
            Material material = Material.getMaterial(key.toUpperCase());
            if (material == null || material == Material.AIR) {
                return new EmptyRecipePart();
            }
            return new SimpleRecipePart(material);
        }

        return lookup.apply(key);
    }
}
