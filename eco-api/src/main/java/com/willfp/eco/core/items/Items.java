package com.willfp.eco.core.items;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.willfp.eco.core.Eco;
import com.willfp.eco.core.fast.FastItemStack;
import com.willfp.eco.core.items.args.LookupArgParser;
import com.willfp.eco.core.items.provider.ItemProvider;
import com.willfp.eco.core.recipe.parts.EmptyTestableItem;
import com.willfp.eco.core.recipe.parts.MaterialTestableItem;
import com.willfp.eco.core.recipe.parts.ModifiedTestableItem;
import com.willfp.eco.core.recipe.parts.TestableStack;
import com.willfp.eco.core.recipe.parts.UnrestrictedMaterialTestableItem;
import com.willfp.eco.util.NamespacedKeyUtils;
import com.willfp.eco.util.NumberUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Class to manage all custom and vanilla items.
 */
public final class Items {
    /**
     * All recipe parts.
     */
    private static final Map<NamespacedKey, TestableItem> REGISTRY = new ConcurrentHashMap<>();

    /**
     * Cached custom item lookups, using {@link HashedItem}.
     */
    private static final LoadingCache<HashedItem, Optional<TestableItem>> CACHE = Caffeine.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build(
                    key -> {
                        if (!key.getItem().hasItemMeta()) {
                            return Optional.empty();
                        }

                        TestableItem match = null;
                        for (TestableItem item : REGISTRY.values()) {
                            if (item.matches(key.getItem())) {
                                match = item;
                                break;
                            }
                        }

                        return Optional.ofNullable(match);
                    }
            );

    /**
     * All item providers.
     */
    private static final Map<String, ItemProvider> PROVIDERS = new ConcurrentHashMap<>();

    /**
     * All recipe parts.
     */
    private static final List<LookupArgParser> ARG_PARSERS = new ArrayList<>();

    /**
     * The handler.
     */
    private static final ItemsLookupHandler ITEMS_LOOKUP_HANDLER = new ItemsLookupHandler(Items::doParse);

    /**
     * Instance of EmptyTestableItem.
     */
    private static final TestableItem EMPTY_TESTABLE_ITEM = new EmptyTestableItem();

    /**
     * Register a new custom item.
     *
     * @param key  The key of the item.
     * @param item The item.
     */
    public static void registerCustomItem(@NotNull final NamespacedKey key,
                                          @NotNull final TestableItem item) {
        REGISTRY.put(key, item);
    }

    /**
     * Register a new item provider.
     *
     * @param provider The provider.
     */
    public static void registerItemProvider(@NotNull final ItemProvider provider) {
        PROVIDERS.put(provider.getNamespace(), provider);
    }

    /**
     * Register a new arg parser.
     *
     * @param parser The parser.
     */
    public static void registerArgParser(@NotNull final LookupArgParser parser) {
        ARG_PARSERS.add(parser);
    }

    /**
     * Remove an item.
     *
     * @param key The key of the recipe part.
     */
    public static void removeCustomItem(@NotNull final NamespacedKey key) {
        REGISTRY.remove(key);
    }

    /**
     * Turn an ItemStack back into a lookup string.
     *
     * @param itemStack The ItemStack.
     * @return The lookup string.
     */
    @NotNull
    public static String toLookupString(@Nullable final ItemStack itemStack) {
        if (itemStack == null) {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        CustomItem customItem = getCustomItem(itemStack);

        if (customItem != null) {
            builder.append(customItem.getKey());
        } else {
            builder.append(itemStack.getType().name().toLowerCase());
        }

        if (itemStack.getAmount() > 1) {
            builder.append(" ")
                    .append(itemStack.getAmount());
        }

        ItemMeta meta = itemStack.getItemMeta();

        if (meta != null) {
            for (LookupArgParser parser : ARG_PARSERS) {
                String parsed = parser.serializeBack(meta);
                if (parsed != null) {
                    builder.append(" ")
                            .append(parsed);
                }
            }
        }

        return builder.toString();
    }

    /**
     * This is the backbone of the entire eco item system.
     * <p>
     * You can look up a TestableItem for any material, custom item,
     * or item in general, and it will return it with any modifiers
     * passed as parameters. This includes stack size (item amount)
     * and enchantments that should be present on the item.
     * <p>
     * If you want to get an ItemStack instance from this, then just call
     * {@link TestableItem#getItem()}.
     * <p>
     * The advantages of the testable item system are that there is the inbuilt
     * {@link TestableItem#matches(ItemStack)} - this allows to check if any item
     * is that testable item; which may sound negligible, but actually it allows for
     * much more power and flexibility. For example, you can have an item with an
     * extra metadata tag, extra lore lines, different display name - and it
     * will still work as long as the test passes. This is very important
     * for custom crafting recipes where other plugins may add metadata
     * values or the play may rename the item.
     *
     * @param key The lookup string.
     * @return The testable item, or an {@link EmptyTestableItem}.
     */
    @NotNull
    public static TestableItem lookup(@NotNull final String key) {
        if (key.startsWith("{")) {
            return Eco.get().testableItemFromSNBT(key);
        }

        return ITEMS_LOOKUP_HANDLER.parseKey(key);
    }

    @NotNull
    private static TestableItem doParse(@NotNull final String[] args) {
        if (args.length == 0) {
            return new EmptyTestableItem();
        }

        TestableItem item = null;

        int stackAmount = 1;

        String[] split = args[0].toLowerCase().split(":");

        if (split.length == 1) {
            String itemType = args[0];
            boolean isWildcard = itemType.startsWith("*");
            if (isWildcard) {
                itemType = itemType.substring(1);
            }
            Material material = Material.getMaterial(itemType.toUpperCase());
            if (material == null || material == Material.AIR) {
                return new EmptyTestableItem();
            }
            item = isWildcard ? new UnrestrictedMaterialTestableItem(material) : new MaterialTestableItem(material);
        }

        if (split.length == 2) {
            String namespace = split[0];
            String keyID = split[1];
            NamespacedKey namespacedKey = NamespacedKeyUtils.create(namespace, keyID);

            TestableItem part = REGISTRY.get(namespacedKey);

            if (part == null && PROVIDERS.containsKey(namespace)) {
                ItemProvider provider = PROVIDERS.get(namespace);

                String reformattedKey = keyID.replace("__", ":");

                part = provider.provideForKey(reformattedKey);
                if (part instanceof EmptyTestableItem || part == null) {
                    return new EmptyTestableItem();
                }
                registerCustomItem(namespacedKey, part);
            }

            /*
            Legacy id:amount format
            This has been superseded by id amount
             */
            if (part == null) {
                String itemType = split[0];
                boolean isWildcard = itemType.startsWith("*");
                if (isWildcard) {
                    itemType = itemType.substring(1);
                }
                Material material = Material.getMaterial(itemType.toUpperCase());
                if (material == null || material == Material.AIR) {
                    return new EmptyTestableItem();
                }
                item = isWildcard ? new UnrestrictedMaterialTestableItem(material) : new MaterialTestableItem(material);
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
            TestableItem part = REGISTRY.get(NamespacedKeyUtils.create(split[0], split[1]));
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

        // Marked as redundant but I am covering all bases here
        if (item == null || item instanceof EmptyTestableItem) {
            return new EmptyTestableItem();
        }

        ItemStack example = item.getItem();
        ItemMeta meta = example.getItemMeta();
        assert meta != null;

        String[] modifierArgs = Arrays.copyOfRange(args, usingNewStackFormat ? 2 : 1, args.length);

        List<Predicate<ItemStack>> predicates = new ArrayList<>();

        for (LookupArgParser argParser : ARG_PARSERS) {
            Predicate<ItemStack> predicate = argParser.parseArguments(modifierArgs, meta);
            if (predicate != null) {
                predicates.add(argParser.parseArguments(modifierArgs, meta));
            }
        }

        example.setItemMeta(meta);
        if (!predicates.isEmpty()) {
            item = new ModifiedTestableItem(
                    item,
                    test -> {
                        for (Predicate<ItemStack> predicate : predicates) {
                            if (!predicate.test(test)) {
                                return false;
                            }
                        }

                        return true;
                    },
                    example
            );
        }

        if (stackAmount <= 1) {
            return item;
        } else {
            return new TestableStack(item, stackAmount);
        }
    }

    /**
     * Get a Testable Item from an ItemStack.
     * <p>
     * Will search for registered items first. If there are no matches in the registry,
     * then it will return a {@link MaterialTestableItem} matching the item type.
     * <p>
     * Does not account for modifiers (arg parser data).
     *
     * @param item The ItemStack.
     * @return The found Testable Item.
     */
    @NotNull
    public static TestableItem getItem(@Nullable final ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return new EmptyTestableItem();
        }

        CustomItem customItem = getCustomItem(item);

        if (customItem != null) {
            return customItem;
        }

        for (TestableItem known : REGISTRY.values()) {
            if (known.matches(item)) {
                return known;
            }
        }
        return new MaterialTestableItem(item.getType());
    }

    /**
     * Get if itemStack is a custom item.
     *
     * @param itemStack The itemStack to check.
     * @return If is recipe.
     */
    public static boolean isCustomItem(@Nullable final ItemStack itemStack) {
        return getCustomItem(itemStack) != null;
    }

    /**
     * Get custom item from item.
     *
     * @param itemStack The item.
     * @return The custom item, or null if not exists.
     */
    @Nullable
    public static CustomItem getCustomItem(@Nullable final ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }

        return CACHE.get(HashedItem.of(itemStack)).map(Items::getOrWrap).orElse(null);
    }

    /**
     * Get all registered custom items.
     *
     * @return A set of all items.
     */
    public static Set<CustomItem> getCustomItems() {
        return REGISTRY.values().stream().map(Items::getOrWrap).collect(Collectors.toSet());
    }

    /**
     * Return a CustomItem instance for a given TestableItem.
     * <p>
     * Used internally since 6.10.0 when the registration moved from {@link CustomItem}
     * to {@link TestableItem} for added flexibility.
     *
     * @param item The item.
     * @return The CustomItem.
     */
    @NotNull
    public static CustomItem getOrWrap(@NotNull final TestableItem item) {
        if (item instanceof CustomItem) {
            return (CustomItem) item;
        } else {
            return new CustomItem(
                    NamespacedKeyUtils.createEcoKey("wrapped_" + NumberUtils.randInt(0, 100000)),
                    item::matches,
                    item.getItem()
            );
        }
    }

    /**
     * Convert an array of materials to an array of testable items.
     *
     * @param materials The materials.
     * @return An array of functionally identical testable items.
     */
    @NotNull
    public static TestableItem[] fromMaterials(@NotNull final Material... materials) {
        return Arrays.stream(materials)
                .map(MaterialTestableItem::new)
                .toArray(MaterialTestableItem[]::new);
    }

    /**
     * Convert a collection of materials into a collection of testable items.
     *
     * @param materials The materials.
     * @return A collection of functionally identical testable items.
     */
    @NotNull
    public static Collection<TestableItem> fromMaterials(@NotNull final Iterable<Material> materials) {
        List<TestableItem> items = new ArrayList<>();
        for (Material material : materials) {
            items.add(new MaterialTestableItem(material));
        }

        return items;
    }

    /**
     * Merge ItemStack onto another ItemStack.
     *
     * @param from The ItemStack to merge from.
     * @param to   The ItemStack to merge onto.
     * @return The ItemStack, merged (same instance as to).
     */
    @NotNull
    public static ItemStack mergeFrom(@NotNull final ItemStack from,
                                      @NotNull final ItemStack to) {
        ItemMeta fromMeta = from.getItemMeta();
        ItemMeta toMeta = to.getItemMeta();

        if (fromMeta == null || toMeta == null) {
            return to;
        }

        ItemMeta newMeta = mergeFrom(fromMeta, toMeta);

        to.setItemMeta(newMeta);
        to.setType(from.getType());
        to.setAmount(from.getAmount());
        return to;
    }

    /**
     * Merge ItemMeta onto other ItemMeta.
     *
     * @param from The ItemMeta to merge from.
     * @param to   The ItemMeta to merge onto.
     * @return The ItemMeta, merged (same instance as to).
     */
    @NotNull
    public static ItemMeta mergeFrom(@NotNull final ItemMeta from,
                                     @NotNull final ItemMeta to) {
        if (from.hasDisplayName()) {
            to.setDisplayName(from.getDisplayName());
        }

        to.setLore(from.getLore());

        for (Enchantment enchant : to.getEnchants().keySet()) {
            to.removeEnchant(enchant);
        }

        for (Map.Entry<Enchantment, Integer> entry : from.getEnchants().entrySet()) {
            to.addEnchant(entry.getKey(), entry.getValue(), true);
        }

        if (from.hasCustomModelData()) {
            to.setCustomModelData(from.getCustomModelData());
        } else {
            to.setCustomModelData(null);
        }

        return to;
    }

    /**
     * Get the base NBT tag on an item.
     *
     * @param itemStack The ItemStack.
     * @return The base NBT.
     */
    @NotNull
    public static PersistentDataContainer getBaseNBT(@NotNull final ItemStack itemStack) {
        return FastItemStack.wrap(itemStack).getBaseTag();
    }

    /**
     * Set the base NBT tag on an item.
     *
     * @param itemStack The ItemStack.
     * @param container The base NBT tag.
     * @return The ItemStack, modified. Not required to use, as this modifies the instance.¬
     */
    @NotNull
    public static ItemStack setBaseNBT(@NotNull final ItemStack itemStack,
                                       @Nullable final PersistentDataContainer container) {
        FastItemStack fis = FastItemStack.wrap(itemStack);
        fis.setBaseTag(container);
        return fis.unwrap();
    }

    /**
     * Convert item to SNBT.
     *
     * @param itemStack The item.
     * @return The NBT string.
     */
    @NotNull
    public static String toSNBT(@NotNull final ItemStack itemStack) {
        return Eco.get().toSNBT(itemStack);
    }

    /**
     * Get item from SNBT.
     *
     * @param snbt The NBT string.
     * @return The ItemStack, or null if invalid.
     */
    @Nullable
    public static ItemStack fromSNBT(@NotNull final String snbt) {
        return Eco.get().fromSNBT(snbt);
    }

    /**
     * Get if an item is empty.
     *
     * @param itemStack The item.
     * @return If empty.
     */
    public static boolean isEmpty(@Nullable final ItemStack itemStack) {
        return EMPTY_TESTABLE_ITEM.matches(itemStack);
    }

    private Items() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
