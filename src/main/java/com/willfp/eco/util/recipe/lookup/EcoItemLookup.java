package com.willfp.eco.util.recipe.lookup;

import com.willfp.eco.util.recipe.parts.EmptyRecipePart;
import com.willfp.eco.util.recipe.parts.RecipePart;
import com.willfp.eco.util.recipe.parts.SimpleRecipePart;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class EcoItemLookup implements ItemLookup {
    /**
     * Set of tests that return if the player is telekinetic.
     */
    private final Map<String, Function<String, RecipePart>> tests = new HashMap<>();

    @Override
    public void registerLookup(@NotNull final String key,
                               @NotNull final Function<String, RecipePart> lookup) {
        tests.put(key, lookup);
    }

    @Override
    public RecipePart lookup(@NotNull final String key) {
        Function<String, RecipePart> lookup = tests.get(key);

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
