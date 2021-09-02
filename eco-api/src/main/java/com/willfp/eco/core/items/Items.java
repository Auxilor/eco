package com.willfp.eco.core.items;

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
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        if (key.contains("?")) {
            String[] options = key.split("\\?");
            for (String option : options) {
                TestableItem lookup = lookup(option);
                if (!(lookup instanceof EmptyTestableItem)) {
                    return lookup;
                }
            }

            return new EmptyTestableItem();
        }

        String[] args = key.split(" ");
        if (args.length == 0) {
            return new EmptyTestableItem();
        }

        TestableItem item = null;

        int stackAmount = 1;

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

            /*
            Legacy id:amount format
            This has been superceded by id amount
             */
            if (part == null) {
                Material material = Material.getMaterial(split[0].toUpperCase());
                if (material == null || material == Material.AIR) {
                    return new EmptyTestableItem();
                }
                item = new MaterialTestableItem(material);
                stackAmount = Integer.parseInt(split[1]);
            } else {
                item = part;
            }
        }

        /*
        Legacy namespace:id:amount format
        This has been superceded by namespace:id amount
         */
        if (split.length == 3) {
            CustomItem part = REGISTRY.get(NamespacedKeyUtils.create(split[0], split[1]));
            if (part == null) {
                return new EmptyTestableItem();
            }
            item = part;
            stackAmount = Integer.parseInt(split[2]);
        }

        boolean usingNewStackFormat = false;

        if (args.length >= 2) {
            try {
                stackAmount = Integer.parseInt(args[1]);
                usingNewStackFormat = true;
            } catch (NumberFormatException ignored) {
            }
        }

        if (item == null) {
            return new EmptyTestableItem();
        }

        String[] enchantArgs = Arrays.copyOfRange(args, usingNewStackFormat ? 2 : 1, args.length);

        Map<Enchantment, Integer> requiredEnchantments = new HashMap<>();

        for (String enchantArg : enchantArgs) {
            String[] enchantArgSplit = enchantArg.split(":");

            Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantArgSplit[0].toLowerCase()));
            int level = Integer.parseInt(enchantArgSplit[1]);

            requiredEnchantments.put(enchantment, level);
        }

        ItemStack example = item.getItem();

        if (example.getItemMeta() instanceof EnchantmentStorageMeta storageMeta) {
            requiredEnchantments.forEach((enchantment, integer) -> storageMeta.addStoredEnchant(enchantment, integer, true));
            example.setItemMeta(storageMeta);
        } else {
            ItemMeta meta = example.getItemMeta();
            assert meta != null;
            requiredEnchantments.forEach((enchantment, integer) -> meta.addEnchant(enchantment, integer, true));
            example.setItemMeta(meta);
        }

        if (!requiredEnchantments.isEmpty()) {
            item = new ModifiedTestableItem(
                    item,
                    itemStack -> {
                        if (!itemStack.hasItemMeta()) {
                            return false;
                        }

                        ItemMeta meta = itemStack.getItemMeta();

                        assert meta != null;

                        if (meta instanceof EnchantmentStorageMeta storageMeta) {
                            for (Map.Entry<Enchantment, Integer> entry : requiredEnchantments.entrySet()) {
                                if (!storageMeta.hasStoredEnchant(entry.getKey())) {
                                    return false;
                                }
                                if (storageMeta.getStoredEnchantLevel(entry.getKey()) < entry.getValue()) {
                                    return false;
                                }
                            }
                        } else {
                            for (Map.Entry<Enchantment, Integer> entry : requiredEnchantments.entrySet()) {
                                if (!meta.hasEnchant(entry.getKey())) {
                                    return false;
                                }
                                if (meta.getEnchantLevel(entry.getKey()) < entry.getValue()) {
                                    return false;
                                }
                            }
                        }

                        return true;
                    },
                    example
            );
        }

        if (stackAmount == 1) {
            return item;
        } else {
            return new TestableStack(item, stackAmount);
        }
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
     * Get custom item from item.
     *
     * @param itemStack The item.
     * @return The custom item, or null if not exists.
     */
    @Nullable
    public CustomItem getCustomItem(@NotNull final ItemStack itemStack) {
        for (CustomItem item : REGISTRY.values()) {
            if (item.matches(itemStack)) {
                return item;
            }
        }
        return null;
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
