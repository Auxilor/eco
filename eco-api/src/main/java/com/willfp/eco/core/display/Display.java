package com.willfp.eco.core.display;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.fast.FastItemStack;
import com.willfp.eco.core.integrations.guidetection.GUIDetectionManager;
import com.willfp.eco.util.NamespacedKeyUtils;
import java.util.*;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class to manage client-side item display.
 * <p>
 * Packet display is not done on the main thread, so make sure
 * all your modules are thread-safe.
 */
public final class Display {
    /**
     * The prefix for client-side lore lines.
     */
    public static final String PREFIX = "§z";

    /**
     * All registered modules, keyed by weight and sorted so that lower weights run first.
     */
    private static final Map<Integer, List<DisplayModule>> REGISTERED_MODULES = new TreeMap<>();

    /**
     * The persistent data key used to mark an item as finalized.
     */
    private static final NamespacedKey FINALIZE_KEY = NamespacedKeyUtils.createEcoKey("finalized");

    /**
     * Display on ItemStacks, with no player context.
     *
     * @param itemStack The item.
     * @return The same ItemStack, modified in place.
     */
    public static ItemStack display(@NotNull final ItemStack itemStack) {
        return display(itemStack, null);
    }

    /**
     * Display on ItemStacks.
     * <p>
     * Generates varargs from every registered module, reverts the item, then runs every
     * module's display in ascending weight order. If the item has no meta and
     * {@code display-without-meta} is disabled in eco's config, the item is returned
     * unchanged after reverting.
     *
     * @param itemStack The item.
     * @param player    The player to display for, or null for no player context.
     * @return The same ItemStack, modified in place.
     */
    public static ItemStack display(@NotNull final ItemStack itemStack,
                                    @Nullable final Player player) {
        Map<String, Object[]> pluginVarArgs = new HashMap<>();

        for (List<DisplayModule> modules : REGISTERED_MODULES.values()) {
            for (DisplayModule module : modules) {
                pluginVarArgs.put(module.getPluginName(), module.generateVarArgs(itemStack));
            }
        }

        Display.revert(itemStack);

        if (!Eco.get().getEcoPlugin().getConfigYml().getBool("display-without-meta")) {
            if (!itemStack.hasItemMeta()) {
                return itemStack;
            }
        }

        ItemStack original = itemStack.clone();
        Inventory inventory = player == null ? null : player.getOpenInventory().getTopInventory();
        boolean inInventory = inventory != null && inventory.contains(original);
        boolean inGui = player != null && GUIDetectionManager.hasGUIOpen(player);

        DisplayProperties properties = new DisplayProperties(
                inInventory,
                inGui,
                original
        );

        for (List<DisplayModule> modules : REGISTERED_MODULES.values()) {
            for (DisplayModule module : modules) {
                Object[] varargs = pluginVarArgs.get(module.getPluginName());

                if (varargs == null) {
                    continue;
                }

                module.display(itemStack, varargs);

                if (player != null) {
                    module.display(itemStack, player, varargs);
                    module.display(itemStack, player, properties, varargs);
                }
            }
        }

        return itemStack;
    }

    /**
     * Display on ItemStacks and then finalize, with no player context.
     *
     * @param itemStack The item.
     * @return The same ItemStack, modified in place.
     */
    public static ItemStack displayAndFinalize(@NotNull final ItemStack itemStack) {
        return finalize(display(itemStack, null));
    }

    /**
     * Display on ItemStacks and then finalize.
     *
     * @param itemStack The item.
     * @param player    The player to display for, or null for no player context.
     * @return The same ItemStack, modified in place.
     */
    public static ItemStack displayAndFinalize(@NotNull final ItemStack itemStack,
                                               @Nullable final Player player) {
        return finalize(display(itemStack, player));
    }

    /**
     * Revert on ItemStacks.
     * <p>
     * Unfinalizes the item, strips the display lore, and then runs every registered module's
     * revert.
     * <p>
     * Display lines are identified on the components themselves, by the shape eco writes them
     * in - see {@link DisplayLines#isDisplayLine(Component)}. Lore added by other plugins is
     * left exactly as it was, including lines that happen to start with {@link #PREFIX}.
     * <p>
     * With {@code use-legacy-lore-revert} enabled in eco's config, every line starting with
     * {@link #PREFIX} is stripped instead, and the lore is round-tripped through legacy
     * strings, which discards anything legacy text can't represent (translatable lines, fonts,
     * hover and click events, sprites). This is how eco used to work.
     *
     * @param itemStack The item.
     * @return The same ItemStack, modified in place.
     */
    public static ItemStack revert(@NotNull final ItemStack itemStack) {
        if (Display.isFinalized(itemStack)) {
            Display.unfinalize(itemStack);
        }

        FastItemStack fast = FastItemStack.wrap(itemStack);

        if (Eco.get().getEcoPlugin().getConfigYml().getBool("use-legacy-lore-revert")) {
            List<String> lore = new ArrayList<>(fast.getLore());
            if (!lore.isEmpty() && lore.removeIf(line -> line.startsWith(Display.PREFIX))) {
                fast.setLore(lore);
            }
        } else {
            List<Component> lore = new ArrayList<>(fast.getLoreComponents());
            if (!lore.isEmpty() && lore.removeIf(DisplayLines::isDisplayLine)) {
                fast.setLoreComponents(lore);
            }
        }

        for (List<DisplayModule> modules : REGISTERED_MODULES.values()) {
            for (DisplayModule module : modules) {
                module.revert(itemStack);
            }
        }

        return itemStack;
    }

    /**
     * Finalize an ItemStack, marking it as not needing display again.
     * <p>
     * Stackable items (max stack size greater than one) are returned unchanged, as the
     * finalize marker would prevent them from stacking.
     *
     * @param itemStack The item.
     * @return The same ItemStack, modified in place.
     */
    public static ItemStack finalize(@NotNull final ItemStack itemStack) {
        if (itemStack.getType().getMaxStackSize() > 1) {
            return itemStack;
        }

        FastItemStack.wrap(itemStack)
                .getPersistentDataContainer()
                .set(FINALIZE_KEY, PersistentDataType.INTEGER, 1);

        return itemStack;
    }

    /**
     * Unfinalize an ItemStack, removing the finalize marker.
     *
     * @param itemStack The item.
     * @return The same ItemStack, modified in place.
     */
    public static ItemStack unfinalize(@NotNull final ItemStack itemStack) {
        FastItemStack.wrap(itemStack)
                .getPersistentDataContainer()
                .remove(FINALIZE_KEY);

        return itemStack;
    }

    /**
     * If an item is finalized.
     *
     * @param itemStack The item.
     * @return If finalized.
     */
    public static boolean isFinalized(@NotNull final ItemStack itemStack) {
        return FastItemStack.wrap(itemStack)
                .getPersistentDataContainer()
                .has(FINALIZE_KEY, PersistentDataType.INTEGER);
    }

    /**
     * Register a new display module.
     * <p>
     * Modules are ordered by {@link DisplayModule#getWeight()}, lowest first.
     *
     * @param module The module.
     */
    public static void registerDisplayModule(@NotNull final DisplayModule module) {
        List<DisplayModule> modules = REGISTERED_MODULES.getOrDefault(
                module.getWeight(),
                new ArrayList<>()
        );

        modules.add(module);

        REGISTERED_MODULES.put(module.getWeight(), modules);
    }

    /**
     * Unregister a display module.
     *
     * @param module The module.
     */
    public static void unregisterDisplayModule(@NotNull final DisplayModule module) {
        for (List<DisplayModule> modules : REGISTERED_MODULES.values()) {
            modules.remove(module);
        }
    }

    /**
     * Utility class, cannot be instantiated.
     */
    private Display() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
