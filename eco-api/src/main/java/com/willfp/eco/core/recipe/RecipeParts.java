package com.willfp.eco.core.recipe;

import com.willfp.eco.core.recipe.parts.EmptyRecipePart;
import com.willfp.eco.core.recipe.parts.RecipePart;
import com.willfp.eco.core.recipe.parts.SimpleRecipePart;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
@SuppressWarnings("deprecation")
public final class RecipeParts {
    /**
     * All recipe parts.
     */
    private static final Map<NamespacedKey, RecipePart> PARTS = new HashMap<>();

    /**
     * Register a new recipe part.
     *
     * @param key  The key of the recipe part.
     * @param part The recipe part.
     */
    public void registerRecipePart(@NotNull final NamespacedKey key,
                                   @NotNull final RecipePart part) {
        PARTS.put(key, part);
    }

    /**
     * Lookup recipe part from string.
     *
     * @param key The string to test.
     * @return The found recipe part, or null if not found.
     */
    public RecipePart lookup(@NotNull final String key) {
        String[] split = key.toLowerCase().split(":");
        if (split.length == 1) {
            Material material = Material.getMaterial(key.toUpperCase());
            if (material == null || material == Material.AIR) {
                return new EmptyRecipePart();
            }
            return new SimpleRecipePart(material);
        }

        RecipePart part = PARTS.get(new NamespacedKey(split[0], split[1]));
        return part == null ? new EmptyRecipePart() : part;
    }

    /**
     * Get if itemStack is a recipe part (used to check for custom items).
     *
     * @param itemStack The itemStack to check.
     * @return If is recipe.
     */
    public boolean isRecipePart(@NotNull final ItemStack itemStack) {
        return PARTS.values().stream().anyMatch(recipePart -> recipePart.matches(itemStack));
    }
}
