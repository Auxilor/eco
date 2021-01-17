package com.willfp.eco.util.recipe.lookup;

import com.willfp.eco.util.recipe.parts.RecipePart;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface ItemLookup {
    /**
     * Register a new lookup.
     *
     * @param key    The key of the lookup.
     * @param lookup The lookup to register, where the output is the recipe part generated.
     */
    void registerLookup(@NotNull String key,
                        @NotNull Function<String, RecipePart> lookup);

    /**
     * Lookup recipe part from string.
     *
     * @param key The string to test.
     * @return The generated recipe part, or null if invalid.
     */
    RecipePart lookup(@NotNull String key);
}
