package com.willfp.eco.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Trident;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

@UtilityClass
public class TridentUtils {
    /**
     * If the meta set function has been set.
     */
    private boolean initialized = false;

    /**
     * The meta set function.
     */
    private Function<Trident, ItemStack> tridentFunction = null;

    /**
     * Get a trident's ItemStack.
     *
     * @param trident The trident to query.
     * @return The trident's ItemStack.
     */
    public ItemStack getItemStack(@NotNull final Trident trident) {
        Validate.isTrue(initialized, "Must be initialized!");
        Validate.notNull(tridentFunction, "Must be initialized!");

        return tridentFunction.apply(trident);
    }

    /**
     * Initialize the trident function.
     *
     * @param function The function.
     */
    @ApiStatus.Internal
    public void initialize(@NotNull final Function<Trident, ItemStack> function) {
        Validate.isTrue(!initialized, "Already initialized!");

        tridentFunction = function;
        initialized = true;
    }
}
