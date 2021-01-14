package com.willfp.eco.util.recipes.lookup;

import com.willfp.eco.util.recipes.parts.EmptyRecipePart;
import com.willfp.eco.util.recipes.parts.RecipePart;
import com.willfp.eco.util.recipes.parts.SimpleRecipePart;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
