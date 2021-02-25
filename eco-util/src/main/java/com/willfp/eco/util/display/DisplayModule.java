package com.willfp.eco.util.display;

import com.willfp.eco.util.internal.PluginDependent;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class DisplayModule extends PluginDependent {
    /**
     * The priority of the module.
     */
    @Getter
    private final DisplayPriority priority;

    /**
     * Create a new display module.
     *
     * @param plugin   The plugin that the display is for.
     * @param priority The priority of the module.
     */
    protected DisplayModule(@NotNull final AbstractEcoPlugin plugin,
                            @NotNull final DisplayPriority priority) {
        super(plugin);
        this.priority = priority;
    }

    /**
     * Display an item.
     *
     * @param itemStack The item.
     */
    protected abstract void display(@NotNull ItemStack itemStack);

    /**
     * Revert an item.
     *
     * @param itemStack The item.
     */
    protected abstract void revert(@NotNull ItemStack itemStack);

    /**
     * Get name of plugin.
     *
     * @return The plugin name.
     */
    String getPluginName() {
        return super.getPlugin().getPluginName();
    }
}
