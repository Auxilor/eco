package com.willfp.eco.core.items;

import com.willfp.eco.core.items.builder.ItemBuilder;
import com.willfp.eco.core.items.builder.ItemStackBuilder;
import com.willfp.eco.core.recipe.parts.EmptyTestableItem;
import com.willfp.eco.core.recipe.parts.MaterialTestableItem;
import com.willfp.eco.core.recipe.parts.ModifiedTestableItem;
import com.willfp.eco.core.recipe.parts.TestableStack;
import com.willfp.eco.util.NamespacedKeyUtils;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class to manage all custom and vanilla items.
 */
@UtilityClass
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
     * Remove an item.
     *
     * @param key The key of the recipe part.
     */
    public void removeCustomItem(@NotNull final NamespacedKey key) {
        REGISTRY.remove(key);
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
        String[] args = key.split(" ");
        if (args.length == 0) {
            return new EmptyTestableItem();
        }

        TestableItem item = null;

        String[] split = args[0].toLowerCase().split(":");

        if (split.length == 1) {
            Material material = Material.getMaterial(args[0].toUpperCase());
            if (material == null || material == Material.AIR) {
                return new EmptyTestableItem();
            }
            item = new MaterialTestableItem(material);
        }

        if (split.length == 2) {
            CustomItem part = REGISTRY.get(NamespacedKeyUtils.create(split[0], split[1]));

            if (part == null) {
                Material material = Material.getMaterial(split[0].toUpperCase());
                if (material == null || material == Material.AIR) {
                    return new EmptyTestableItem();
                }
                item = new TestableStack(new MaterialTestableItem(material), Integer.parseInt(split[1]));
            } else {
                item = part;
            }
        }

        if (split.length == 3) {
            CustomItem part = REGISTRY.get(NamespacedKeyUtils.create(split[0], split[1]));
            item = part == null ? new EmptyTestableItem() : new TestableStack(part, Integer.parseInt(split[2]));
        }

        if (item == null || item instanceof EmptyTestableItem) {
            return new EmptyTestableItem();
        }

        String[] enchantArgs = Arrays.copyOfRange(args, 1, args.length);

        Map<Enchantment, Integer> requiredEnchantments = new HashMap<>();

        for (String enchantArg : enchantArgs) {
            String[] enchantArgSplit = enchantArg.split(":");

            Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantArgSplit[0].toLowerCase()));
            int level = Integer.parseInt(enchantArgSplit[1]);

            requiredEnchantments.put(enchantment, level);
        }

        if (requiredEnchantments.isEmpty()) {
            return item;
        }

        ItemBuilder builder = new ItemStackBuilder(item.getItem());
        requiredEnchantments.forEach(builder::addEnchantment);
        ItemStack example = builder.build();

        return new ModifiedTestableItem(
                item,
                itemStack -> {
                    if (!itemStack.hasItemMeta()) {
                        return false;
                    }

                    ItemMeta meta = itemStack.getItemMeta();

                    assert meta != null;

                    for (Map.Entry<Enchantment, Integer> entry : requiredEnchantments.entrySet()) {
                        if (!meta.hasEnchant(entry.getKey())) {
                            return false;
                        }
                        if (meta.getEnchantLevel(entry.getKey()) < entry.getValue()) {
                            return false;
                        }
                    }

                    return true;
                },
                example
        );
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

    /**
     * Get all registered custom items.
     *
     * @return A set of all items.
     */
    public Set<CustomItem> getCustomItems() {
        return new HashSet<>(REGISTRY.values());
    }
}
