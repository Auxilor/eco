package com.willfp.eco.util.display;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class DisplayModule {
    /**
     * Priority of the display module, where lower numbers are executed sooner.
     */
    @Getter
    private final int priority;

    /**
     * The function executed on display.
     */
    @Getter
    private final Function<ItemStack, ItemStack> function;

    /**
     * Create new display module.
     *
     * @param function The function.
     * @param priority The priority.
     */
    public DisplayModule(@NotNull final Function<ItemStack, ItemStack> function,
                         final int priority) {
        this.function = function;
        this.priority = priority;
    }
}
