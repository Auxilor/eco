package com.willfp.eco.core.display;

import com.willfp.eco.core.fast.FastItemStack;
import com.willfp.eco.util.NamespacedKeyUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Utility class to manage client-side item display.
 */
public final class Display {
    /**
     * The prefix for client-side lore lines.
     */
    public static final String PREFIX = "§z";

    /**
     * All registered modules.
     */
    private static final Map<Integer, List<DisplayModule>> REGISTERED_MODULES = new TreeMap<>();

    /**
     * The finalize key.
     */
    private static final NamespacedKey FINALIZE_KEY = NamespacedKeyUtils.createEcoKey("finalized");

    /**
     * Display on ItemStacks.
     *
     * @param itemStack The item.
     * @return The ItemStack.
     */
    public static ItemStack display(@NotNull final ItemStack itemStack) {
        return display(itemStack, null);
    }

    /**
     * Display on ItemStacks.
     *
     * @param itemStack The item.
     * @param player    The player.
     * @return The ItemStack.
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

        if (!itemStack.hasItemMeta()) {
            return itemStack;
        }

        ItemStack original = itemStack.clone();
        Inventory inventory = player == null ? null : player.getOpenInventory().getTopInventory();
        boolean inInventory = inventory != null && inventory.contains(original);
        boolean inGui = inventory != null && inventory.getHolder() == null;

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
     * Display on ItemStacks and then finalize.
     *
     * @param itemStack The item.
     * @return The ItemStack.
     */
    public static ItemStack displayAndFinalize(@NotNull final ItemStack itemStack) {
        return finalize(display(itemStack, null));
    }

    /**
     * Display on ItemStacks and then finalize.
     *
     * @param itemStack The item.
     * @param player    The player.
     * @return The ItemStack.
     */
    public static ItemStack displayAndFinalize(@NotNull final ItemStack itemStack,
                                               @Nullable final Player player) {
        return finalize(display(itemStack, player));
    }

    /**
     * Revert on ItemStacks.
     *
     * @param itemStack The item.
     * @return The ItemStack.
     */
    public static ItemStack revert(@NotNull final ItemStack itemStack) {
        if (Display.isFinalized(itemStack)) {
            Display.unfinalize(itemStack);
        }

        FastItemStack fast = FastItemStack.wrap(itemStack);

        List<String> lore = fast.getLore();

        if (!lore.isEmpty() && lore.removeIf(line -> line.startsWith(Display.PREFIX))) {
            fast.setLore(lore);
        }

        for (List<DisplayModule> modules : REGISTERED_MODULES.values()) {
            for (DisplayModule module : modules) {
                module.revert(itemStack);
            }
        }

        return itemStack;
    }

    /**
     * Finalize an ItemStacks.
     *
     * @param itemStack The item.
     * @return The ItemStack.
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
     * Unfinalize an ItemStacks.
     *
     * @param itemStack The item.
     * @return The ItemStack.
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
     *
     * @param module The module.
     */
    public static void registerDisplayModule(@NotNull final DisplayModule module) {
        List<DisplayModule> modules = REGISTERED_MODULES.getOrDefault(
                module.getWeight(),
                new ArrayList<>()
        );

        modules.removeIf(it -> it.getPluginName().equalsIgnoreCase(module.getPluginName()));
        modules.add(module);

        REGISTERED_MODULES.put(module.getWeight(), modules);
    }

    private Display() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
