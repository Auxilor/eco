package com.willfp.eco.core.display;

import com.willfp.eco.core.EcoPlugin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Class for all plugin-specific client-side item display modules.
 * <p>
 * Display modules are called in the netty thread, so make sure they are thread-safe.
 * <p>
 * Register modules by returning them from
 * {@link EcoPlugin#loadDisplayModules()}, or directly with
 * {@link Display#registerDisplayModule(DisplayModule)}.
 */
public abstract class DisplayModule {
    /**
     * The weight of the module; modules with a lower weight are displayed first.
     */
    private final int weight;

    /**
     * The plugin.
     */
    private final EcoPlugin plugin;

    /**
     * Create a new display module.
     *
     * @param plugin   The plugin that the display is for.
     * @param priority The priority of the module.
     */
    protected DisplayModule(@NotNull final EcoPlugin plugin,
                            @NotNull final DisplayPriority priority) {
        this(plugin, priority.getWeight());
    }

    /**
     * Create a new display module.
     *
     * @param plugin The plugin that the display is for.
     * @param weight The weight/priority of the module.
     */
    protected DisplayModule(@NotNull final EcoPlugin plugin,
                            final int weight) {
        this.plugin = plugin;
        this.weight = weight;
    }

    /**
     * Display an item, with no player context.
     * <p>
     * Does nothing by default; override when needed.
     *
     * @param itemStack The item.
     * @param args      The varargs from {@link #generateVarArgs(ItemStack)}.
     */
    public void display(@NotNull final ItemStack itemStack,
                        @NotNull final Object... args) {
        // Technically optional.
    }

    /**
     * Display an item for a player.
     * <p>
     * Only called when a player was passed into {@link Display#display(ItemStack, Player)}.
     * <p>
     * Does nothing by default; override when needed.
     *
     * @param itemStack The item.
     * @param player    The player, or null if there is no player context.
     * @param args      The varargs from {@link #generateVarArgs(ItemStack)}.
     */
    public void display(@NotNull final ItemStack itemStack,
                        @Nullable final Player player,
                        @NotNull final Object... args) {
        // Technically optional.
    }

    /**
     * Display an item for a player, with extra context.
     * <p>
     * Only called when a player was passed into {@link Display#display(ItemStack, Player)}.
     * <p>
     * Does nothing by default; override when needed.
     *
     * @param itemStack  The item.
     * @param player     The player, or null if there is no player context.
     * @param properties The properties.
     * @param args       The varargs from {@link #generateVarArgs(ItemStack)}.
     */
    public void display(@NotNull final ItemStack itemStack,
                        @Nullable final Player player,
                        @NotNull final DisplayProperties properties,
                        @NotNull final Object... args) {
        // Technically optional.
    }

    /**
     * Revert an item, undoing anything this module added during display.
     * <p>
     * Does nothing by default; override when needed.
     *
     * @param itemStack The item.
     */
    public void revert(@NotNull final ItemStack itemStack) {
        // Technically optional.
    }

    /**
     * Create varargs to pass back to ItemStack after reverting, but before display.
     * <p>
     * Called for every module before the item is reverted, and the result is then passed
     * into this module's display methods. This is how state can be carried across the
     * revert.
     * <p>
     * Returns an empty array by default; override when needed.
     *
     * @param itemStack The itemStack.
     * @return The plugin-specific varargs.
     */
    public Object[] generateVarArgs(@NotNull final ItemStack itemStack) {
        return new Object[0];
    }

    /**
     * Get name of plugin.
     *
     * @return The plugin name.
     */
    public final String getPluginName() {
        return this.getPlugin().getName();
    }

    /**
     * Get the display weight.
     *
     * @return The weight; modules with a lower weight are displayed first.
     */
    public int getWeight() {
        return this.weight;
    }

    /**
     * Get the plugin.
     *
     * @return The plugin.
     */
    public EcoPlugin getPlugin() {
        return plugin;
    }
}
