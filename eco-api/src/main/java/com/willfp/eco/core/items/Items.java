package com.willfp.eco.core.items;

import com.willfp.eco.core.recipe.parts.EmptyTestableItem;
import com.willfp.eco.core.recipe.parts.MaterialTestableItem;
import com.willfp.eco.core.recipe.parts.ModifiedTestableItem;
import com.willfp.eco.core.recipe.parts.TestableStack;
import com.willfp.eco.util.NamespacedKeyUtils;
import com.willfp.eco.util.SkullUtils;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class to manage all custom and vanilla items.
 */
@UtilityClass
public final class Items {
    /**
     * All recipe parts.
     */
    private static final Map<NamespacedKey, CustomItem> REGISTRY = new ConcurrentHashMap<>();

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
     * This is the backbone of the entire eco item system.
     * <p>
     * You can lookup a TestableItem for any material, custom item,
     * or item in general, and it will return it with any modifiers
     * passed as parameters. This includes stack size (item amount)
     * and enchantments that should be present on the item.
     * <p>
     * If you want to get an ItemStack instance from this, then just call
     * {@link TestableItem#getItem()}.
     * <p>
     * The advantages of the testable item system are that there is the inbuilt
     * {@link TestableItem#matches(ItemStack)} - this allows to check if any item
     * is that testable item; which may sound negligible but actually it allows for
     * much more power an flexibility. For example, you can have an item with an
     * extra metadata tag, extra lore lines, different display name - and it
     * will still work as long as the test passes. This is very important
     * for custom crafting recipes where other plugins may add metadata
     * values or the play may rename the item.
     *
     * @param key The lookup string.
     * @return The testable item, or an {@link EmptyTestableItem}.
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
            This has been superseded by id amount
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
        This has been superseded by namespace:id amount
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

        ItemStack example = item.getItem();
        ItemMeta meta = example.getItemMeta();
        assert meta != null;

        String[] modifierArgs = Arrays.copyOfRange(args, usingNewStackFormat ? 2 : 1, args.length);

        boolean hasModifiers = false;
        Map<Enchantment, Integer> requiredEnchantments = new HashMap<>();
        String skullTexture = null;

        // Handle skull texture
        for (String arg : modifierArgs) {
            String[] argSplit = arg.split(":");
            if (!argSplit[0].equalsIgnoreCase("texture")) {
                continue;
            }

            if (argSplit.length < 2) {
                continue;
            }

            skullTexture = argSplit[1];
            hasModifiers = true;
        }

        if (meta instanceof SkullMeta skullMeta && skullTexture != null) {
            SkullUtils.setSkullTexture(skullMeta, skullTexture);
        }

        // Handle enchantment modifiers
        for (String enchantArg : modifierArgs) {
            String[] enchantArgSplit = enchantArg.split(":");

            Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantArgSplit[0].toLowerCase()));
            if (enchantment == null) {
                continue;
            }

            if (enchantArgSplit.length < 2) {
                continue;
            }

            int level = Integer.parseInt(enchantArgSplit[1]);

            requiredEnchantments.put(enchantment, level);
            hasModifiers = true;
        }

        if (meta instanceof EnchantmentStorageMeta storageMeta) {
            requiredEnchantments.forEach((enchantment, integer) -> storageMeta.addStoredEnchant(enchantment, integer, true));
        } else {
            requiredEnchantments.forEach((enchantment, integer) -> meta.addEnchant(enchantment, integer, true));
        }

        /*
        The modifiers are then applied.
         */

        example.setItemMeta(meta);

        String finalSkullTexture = skullTexture; // I hate this, java.
        if (hasModifiers) {
            item = new ModifiedTestableItem(
                    item,
                    test -> {
                        if (!test.hasItemMeta()) {
                            return false;
                        }

                        ItemMeta testMeta = test.getItemMeta();

                        assert testMeta != null;

                        if (testMeta instanceof EnchantmentStorageMeta storageMeta) {
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
                                if (!testMeta.hasEnchant(entry.getKey())) {
                                    return false;
                                }
                                if (testMeta.getEnchantLevel(entry.getKey()) < entry.getValue()) {
                                    return false;
                                }
                            }
                        }

                        if (testMeta instanceof SkullMeta skullMeta && finalSkullTexture != null) {
                            if (!finalSkullTexture.equalsIgnoreCase(SkullUtils.getSkullTexture(skullMeta))) {
                                return false;
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
