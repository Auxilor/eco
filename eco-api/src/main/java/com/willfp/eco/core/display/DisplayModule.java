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
 */
public abstract class DisplayModule {
    /**
     * The priority of the module.
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
     * Display an item.
     *
     * @param itemStack The item.
     * @param args      Optional args for display.
     */
    public void display(@NotNull final ItemStack itemStack,
                        @NotNull final Object... args) {
        // Technically optional.
    }

    /**
     * Display an item.
     *
     * @param itemStack The item.
     * @param player    The player.
     * @param args      Optional args for display.
     */
    public void display(@NotNull final ItemStack itemStack,
                        @Nullable final Player player,
                        @NotNull final Object... args) {
        // Technically optional.
    }

    /**
     * Display an item.
     *
     * @param itemStack  The item.
     * @param player     The player.
     * @param properties The properties.
     * @param args       Optional args for display.
     */
    public void display(@NotNull final ItemStack itemStack,
                        @Nullable final Player player,
                        @NotNull final DisplayProperties properties,
                        @NotNull final Object... args) {
        // Technically optional.
    }

    /**
     * Revert an item.
     *
     * @param itemStack The item.
     */
    public void revert(@NotNull final ItemStack itemStack) {
        // Technically optional.
    }

    /**
     * Create varargs to pass back to ItemStack after reverting, but before display.
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
     * @return The weight.
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
