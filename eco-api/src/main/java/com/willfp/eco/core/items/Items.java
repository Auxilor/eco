package com.willfp.eco.core.items;

import com.willfp.eco.core.items.args.LookupArgParser;
import com.willfp.eco.core.items.provider.ItemProvider;
import com.willfp.eco.core.recipe.parts.EmptyTestableItem;
import com.willfp.eco.core.recipe.parts.MaterialTestableItem;
import com.willfp.eco.core.recipe.parts.ModifiedTestableItem;
import com.willfp.eco.core.recipe.parts.TestableStack;
import com.willfp.eco.util.NamespacedKeyUtils;
import com.willfp.eco.util.NumberUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
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
     * All item providers.
     */
    private static final Map<String, ItemProvider> PROVIDERS = new ConcurrentHashMap<>();

    /**
     * All recipe parts.
     */
    private static final List<LookupArgParser> ARG_PARSERS = new ArrayList<>();

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

        String[] args = parseLookupString(key);

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
            String namespace = split[0];
            String keyID = split[1];
            NamespacedKey namespacedKey = NamespacedKeyUtils.create(namespace, keyID);

            TestableItem part = REGISTRY.get(namespacedKey);

            if (part == null && PROVIDERS.containsKey(namespace)) {
                ItemProvider provider = PROVIDERS.get(namespace);
                item = provider.provideForKey(keyID);
                if (item instanceof EmptyTestableItem || item == null) {
                    return new EmptyTestableItem();
                }
                registerCustomItem(namespacedKey, item);
            }

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

        // Marked as redundant but i am covering all bases here
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
     * Parse lookup string into arguments.
     * <p>
     * Handles quoted strings for names.
     *
     * @param lookup The lookup string.
     * @return An array of arguments to be processed.
     * @author Shawn (https://stackoverflow.com/questions/70606170/split-a-list-on-spaces-and-group-quoted-characters/70606653#70606653)
     */
    @NotNull
    public static String[] parseLookupString(@NotNull final String lookup) {
        char[] chars = lookup.toCharArray();
        List<String> arguments = new ArrayList<>();
        StringBuilder argumentBuilder = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == ' ') {
                /*
                Take the current value of the argument builder, append it to the
                list of found arguments, and then clear it for the next argument.
                 */
                arguments.add(argumentBuilder.toString());
                argumentBuilder.setLength(0);
            } else if (chars[i] == '"') {
                /*
                Work until the next unescaped quote to handle quotes with
                spaces in them - assumes the input string is well-formatted
                 */
                for (i++; chars[i] != '"'; i++) {
                    /*
                    If the found quote is escaped, ignore it in the parsing
                     */
                    if (chars[i] == '\\') {
                        i++;
                    }
                    argumentBuilder.append(chars[i]);
                }
            } else {
                /*
                If it's a regular character, just append it to the current argument.
                 */
                argumentBuilder.append(chars[i]);
            }
        }
        arguments.add(argumentBuilder.toString()); // Adds the last argument to the arguments.
        return arguments.toArray(new String[0]);
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
    public static boolean isCustomItem(@NotNull final ItemStack itemStack) {
        for (TestableItem item : REGISTRY.values()) {
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
    public static CustomItem getCustomItem(@NotNull final ItemStack itemStack) {
        for (TestableItem item : REGISTRY.values()) {
            if (item.matches(itemStack)) {
                return getOrWrap(item);
            }
        }
        return null;
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

    private Items() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
