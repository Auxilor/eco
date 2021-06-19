package com.willfp.eco.core.items;

import com.willfp.eco.core.recipe.parts.EmptyTestableItem;
import com.willfp.eco.core.recipe.parts.MaterialTestableItem;
import com.willfp.eco.core.recipe.parts.TestableStack;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
@SuppressWarnings("deprecation")
public final class Items {
    /**
     * All recipe parts.
     */
    private static final Map<NamespacedKey, CustomItem> REGISTRY = new HashMap<>();

    /**
     * Register a new recipe part.
     *
     * @param key  The key of the recipe part.
     * @param part The recipe part.
     */
    public void registerCustomItem(@NotNull final NamespacedKey key,
                                   @NotNull final CustomItem part) {
        REGISTRY.put(key, part);
    }

    /**
     * Lookup item from string.
     * <p>
     * Used for recipes.
     *
     * @param key The string to test.
     * @return The found testable item, or an empty item if not found.
     */
    public TestableItem lookup(@NotNull final String key) {
        String[] split = key.toLowerCase().split(":");

        if (split.length == 1) {
            Material material = Material.getMaterial(key.toUpperCase());
            if (material == null || material == Material.AIR) {
                return new EmptyTestableItem();
            }
            return new MaterialTestableItem(material);
        }

        if (split.length == 2) {
            CustomItem part = REGISTRY.get(new NamespacedKey(split[0], split[1]));

            if (part == null) {
                Material material = Material.getMaterial(split[0].toUpperCase());
                if (material == null || material == Material.AIR) {
                    return new EmptyTestableItem();
                }
                return new TestableStack(new MaterialTestableItem(material), Integer.parseInt(split[1]));
            } else {
                return part;
            }
        }

        if (split.length == 3) {
            CustomItem part = REGISTRY.get(new NamespacedKey(split[0], split[1]));
            return part == null ? new EmptyTestableItem() : new TestableStack(part, Integer.parseInt(split[2]));
        }

        return new EmptyTestableItem();
    }

    /**
     * Get if itemStack is a custom item.
     *
     * @param itemStack The itemStack to check.
     * @return If is recipe.
     */
    public boolean isCustomItem(@NotNull final ItemStack itemStack) {
        for (CustomItem item : REGISTRY.values()) {
            if (item.matches(itemStack)) {
                return true;
            }
        }
        return false;
    }
}
