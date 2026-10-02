package com.willfp.eco.core.display;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.fast.FastItemStack;
import com.willfp.eco.core.integrations.guidetection.GUIDetectionManager;
import com.willfp.eco.util.NamespacedKeyUtils;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.ApiStatus;
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
     *
     * @deprecated Add display lines through {@link DisplayLore} from
     * {@link DisplayModule#display(DisplayContext)}, which marks them without text. eco still
     * recognises lines starting with this prefix on stored items. Scheduled for removal in
     * 2027.39.
     */
    @Deprecated(since = "2026.39", forRemoval = true)
    @ApiStatus.ScheduledForRemoval(inVersion = "2027.39")
    public static final String PREFIX = DisplayLines.LEGACY_PREFIX;

    /**
     * All registered modules.
     */
    private static final DisplayModuleRegistry REGISTRY = new DisplayModuleRegistry();

    /**
     * Increases whenever displayed items may have changed, such as on reload.
     */
    private static final AtomicInteger GENERATION = new AtomicInteger();

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
     * Restores an item that was already displayed, generates varargs from every registered
     * module, reverts the item, then runs every module's display in ascending weight order, with
     * modules adding lore through {@link DisplayLore}. Air is returned unchanged. If the item has no meta and
     * {@code display-without-meta} is disabled in eco's config, the item is returned unchanged
     * after reverting.
     * <p>
     * If display changed anything, a record of it is written onto the item, so that
     * {@link #revert(ItemStack)} restores it exactly.
     *
     * @param itemStack The item.
     * @param player    The player to display for, or null for no player context.
     * @return The same ItemStack, modified in place.
     */
    public static ItemStack display(@NotNull final ItemStack itemStack,
                                    @Nullable final Player player) {
        return display(itemStack, player, null);
    }

    /**
     * Display on ItemStacks, with a faster check for whether the item is in the open inventory.
     *
     * @param itemStack        The item.
     * @param player           The player to display for, or null for no player context.
     * @param inInventoryCheck Checks if the item is in the player's open inventory, or null to
     *                         search the inventory.
     * @return The same ItemStack, modified in place.
     */
    @SuppressWarnings("deprecation")
    @ApiStatus.Internal
    public static ItemStack display(@NotNull final ItemStack itemStack,
                                    @Nullable final Player player,
                                    @Nullable final Predicate<ItemStack> inInventoryCheck) {
        if (itemStack.getType().isAir()) {
            return itemStack;
        }

        DisplayRecorder recorder = Eco.get().getDisplayRecorder();
        ItemStack displayed = recorder.mirror(itemStack);
        displayMirror(displayed, player, inInventoryCheck, recorder);

        if (displayed != itemStack) {
            if (itemStack.getType() != displayed.getType()) {
                itemStack.setType(displayed.getType());
            }

            itemStack.setAmount(displayed.getAmount());
            itemStack.setItemMeta(displayed.getItemMeta());
        }

        return itemStack;
    }

    @SuppressWarnings("removal")
    private static void displayMirror(@NotNull final ItemStack itemStack,
                                      @Nullable final Player player,
                                      @Nullable final Predicate<ItemStack> inInventoryCheck,
                                      @NotNull final DisplayRecorder recorder) {
        List<DisplayModule> modules = REGISTRY.getModules();
        recorder.restore(itemStack);
        ItemStack snapshot = itemStack.clone();
        Map<DisplayModule, Object[]> moduleVarArgs = new IdentityHashMap<>();

        for (DisplayModule module : modules) {
            moduleVarArgs.put(module, module.generateVarArgs(itemStack));
        }

        Display.revert(itemStack);

        if (!Eco.get().getEcoPlugin().getConfigYml().getBool("display-without-meta")) {
            if (!itemStack.hasItemMeta()) {
                return;
            }
        }

        ItemStack original = itemStack.clone();

        DisplayProperties properties = new DisplayProperties(
                isInInventory(original, player, inInventoryCheck),
                player != null && GUIDetectionManager.hasGUIOpen(player),
                original
        );

        LoreSync sync = new LoreSync(
                recorder,
                itemStack,
                Eco.get().getEcoPlugin().getConfigYml().getBool("display-legacy-prefix-marker")
        );
        DisplayLoreBuilder lore = DisplayLoreBuilder.ofForeign(sync.getLore());

        for (DisplayModule module : modules) {
            Object[] varargs = moduleVarArgs.get(module);

            if (varargs == null) {
                continue;
            }

            module.display(new EcoDisplayContext(itemStack, player, properties, varargs, lore));

            if (!LegacyDisplayModules.overridesLegacyDisplay(module)) {
                continue;
            }

            sync.syncLore(lore);

            module.display(itemStack, varargs);

            if (player != null) {
                module.display(itemStack, player, varargs);
                module.display(itemStack, player, properties, varargs);
            }

            lore = sync.readBack(lore);
        }

        lore.removeStaleLines();
        sync.finish(lore);

        recorder.record(itemStack, snapshot, lore.getDisplayIndices());
    }

    private static boolean isInInventory(@NotNull final ItemStack original,
                                         @Nullable final Player player,
                                         @Nullable final Predicate<ItemStack> inInventoryCheck) {
        if (player == null) {
            return false;
        }

        if (inInventoryCheck != null) {
            return inInventoryCheck.test(original);
        }

        Inventory inventory = player.getOpenInventory().getTopInventory();

        return inventory != null && inventory.contains(original);
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
     * An item carrying a display record is restored exactly from it instead, and module reverts
     * do not run. A record that is not valid, such as one not written by this server, is
     * removed and the item is reverted as if it had none.
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
    @SuppressWarnings("removal")
    public static ItemStack revert(@NotNull final ItemStack itemStack) {
        boolean restored = Eco.get().getDisplayRecorder().restore(itemStack);
        FastItemStack fast = FastItemStack.wrap(itemStack);
        PersistentDataContainer container = fast.getPersistentDataContainer();

        if (container.has(FINALIZE_KEY, PersistentDataType.INTEGER)) {
            container.remove(FINALIZE_KEY);
        }

        if (restored) {
            return itemStack;
        }

        if (Eco.get().getEcoPlugin().getConfigYml().getBool("use-legacy-lore-revert")) {
            List<String> lore = new ArrayList<>(fast.getLore());
            if (!lore.isEmpty() && lore.removeIf(line -> line.startsWith(DisplayLines.LEGACY_PREFIX))) {
                fast.setLore(lore);
            }
        } else {
            List<Component> lore = new ArrayList<>(fast.getLoreComponents());
            if (!lore.isEmpty() && lore.removeIf(DisplayLines::isDisplayLine)) {
                fast.setLoreComponents(lore);
            }
        }

        for (DisplayModule module : REGISTRY.getModules()) {
            module.revert(itemStack);
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
        REGISTRY.register(module);
    }

    /**
     * Unregister a display module.
     *
     * @param module The module.
     */
    public static void unregisterDisplayModule(@NotNull final DisplayModule module) {
        REGISTRY.unregister(module);
    }

    /**
     * If a lore line was added by eco's display.
     *
     * @param line The lore line.
     * @return If the line is a display line.
     */
    public static boolean isDisplayLine(@NotNull final Component line) {
        return DisplayLines.isDisplayLine(line);
    }

    /**
     * The content of a lore line without eco's marker or {@link #PREFIX}.
     * <p>
     * Use this when copying lore from a stored item, which may start with the prefix, into
     * {@link DisplayLore}.
     *
     * @param line The lore line.
     * @return The content.
     */
    @NotNull
    public static Component stripDisplayMarker(@NotNull final Component line) {
        return DisplayLines.withoutPrefix(line);
    }

    /**
     * The current display generation.
     *
     * @return The generation.
     */
    @ApiStatus.Internal
    public static int getGeneration() {
        return GENERATION.get();
    }

    /**
     * Mark every cached display as out of date.
     */
    @ApiStatus.Internal
    public static void invalidate() {
        GENERATION.incrementAndGet();
    }

    /**
     * Keeps the lore on an item in step with the lore display modules build, so that it is only
     * written when modules changed it, and only read back when a legacy module changed it. Lore
     * that legacy modules changed is rendered once, after all modules.
     */
    private static final class LoreSync {
        /**
         * The display recorder.
         */
        private final DisplayRecorder recorder;

        /**
         * The item.
         */
        private final ItemStack itemStack;

        /**
         * If display lines get the legacy prefix.
         */
        private final boolean legacyPrefix;

        /**
         * The lore last read from or written to the item.
         */
        private List<Component> lore;

        /**
         * The lore state of the item when the lore was last read or written.
         */
        private Object loreState;

        /**
         * If no legacy module changed the lore since it was last rendered.
         */
        private boolean rendered = true;

        /**
         * Create a lore sync, reading the lore on the item.
         *
         * @param recorder     The display recorder.
         * @param itemStack    The item.
         * @param legacyPrefix If display lines get the legacy prefix.
         */
        private LoreSync(@NotNull final DisplayRecorder recorder,
                         @NotNull final ItemStack itemStack,
                         final boolean legacyPrefix) {
            this.recorder = recorder;
            this.itemStack = itemStack;
            this.legacyPrefix = legacyPrefix;
            this.lore = FastItemStack.wrap(itemStack).getLoreComponents();
            this.loreState = recorder.getLoreState(itemStack);
        }

        /**
         * Get the lore last read from or written to the item.
         *
         * @return The lore.
         */
        @NotNull
        private List<Component> getLore() {
            return this.lore;
        }

        /**
         * Write the built lore to the item, if it changed since it was last read or written.
         *
         * @param builder The lore builder.
         */
        private void syncLore(@NotNull final DisplayLoreBuilder builder) {
            if (builder.isSynced() && !this.changedOnItem()) {
                return;
            }

            this.render(builder);
        }

        /**
         * Write the built lore to the item after all modules, if it changed or a legacy module
         * changed it since it was last rendered.
         *
         * @param builder The lore builder.
         */
        private void finish(@NotNull final DisplayLoreBuilder builder) {
            if (this.rendered) {
                this.syncLore(builder);
            } else {
                this.render(builder);
            }
        }

        private void render(@NotNull final DisplayLoreBuilder builder) {
            this.lore = builder.render(this.legacyPrefix);
            this.recorder.setLore(this.itemStack, this.lore);
            this.loreState = this.recorder.getLoreState(this.itemStack);
            this.rendered = true;
            builder.markSynced();
        }

        /**
         * Read back lore that a legacy module set on the item.
         *
         * @param builder The lore builder.
         * @return A builder matching the lore on the item.
         */
        @NotNull
        private DisplayLoreBuilder readBack(@NotNull final DisplayLoreBuilder builder) {
            if (!this.changedOnItem()) {
                return builder;
            }

            List<Component> itemLore = FastItemStack.wrap(this.itemStack).getLoreComponents();
            DisplayLoreBuilder matched = new DisplayLoreBuilder(
                    LegacyLoreMatcher.match(builder.getNodes(), this.lore, itemLore)
            );
            this.lore = itemLore;
            this.loreState = this.recorder.getLoreState(this.itemStack);
            this.rendered = false;
            matched.markSynced();

            return matched;
        }

        private boolean changedOnItem() {
            return this.recorder.getLoreState(this.itemStack) != this.loreState;
        }
    }

    /**
     * Utility class, cannot be instantiated.
     */
    private Display() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
