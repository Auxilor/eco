package com.willfp.eco.core.display;

import com.willfp.eco.core.PluginDependent;
import com.willfp.eco.core.EcoPlugin;
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
    protected DisplayModule(@NotNull final EcoPlugin plugin,
                            @NotNull final DisplayPriority priority) {
        super(plugin);
        this.priority = priority;
    }

    /**
     * Display an item.
     *
     * @param itemStack The item.
     * @param args      Optional args for display.
     */
    protected void display(@NotNull final ItemStack itemStack,
                           @NotNull final Object... args) {
        // Technically optional.
    }

    /**
     * Revert an item.
     *
     * @param itemStack The item.
     */
    protected void revert(@NotNull final ItemStack itemStack) {
        // Technically optional.
    }

    /**
     * Create varargs to pass back to itemstack after reverting, but before display.
     *
     * @param itemStack The itemStack.
     * @return The plugin-specific varargs.
     */
    protected Object[] generateVarArgs(@NotNull final ItemStack itemStack) {
        return new Object[0];
    }

    /**
     * Get name of plugin.
     *
     * @return The plugin name.
     */
    final String getPluginName() {
        return super.getPlugin().getPluginName();
    }
}
