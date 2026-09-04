package com.willfp.eco.core.items;

import java.util.List;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * The result of applying data components to an item.
 * <p>
 * Components are applied independently, so an invalid component does not stop
 * the valid ones from being applied: the item is always returned, and anything
 * that could not be applied is described in {@link #getErrors()}.
 */
public final class ItemComponentResult {
    /**
     * The item with the components applied.
     */
    private final ItemStack item;

    /**
     * The components that could not be applied, as human-readable messages.
     */
    private final List<String> errors;

    /**
     * Create a new component result.
     *
     * @param item   The item with the components applied.
     * @param errors The components that could not be applied.
     */
    public ItemComponentResult(@NotNull final ItemStack item,
                               @NotNull final List<String> errors) {
        this.item = item;
        this.errors = List.copyOf(errors);
    }

    /**
     * Get the item with the components applied.
     *
     * @return The item.
     */
    @NotNull
    public ItemStack getItem() {
        return this.item;
    }

    /**
     * Get the components that could not be applied.
     * <p>
     * Each message names the component and why it was rejected, for example
     * <code>minecraft:food: invalid value</code>.
     *
     * @return The errors, or an empty list if every component was applied.
     */
    @NotNull
    public List<String> getErrors() {
        return this.errors;
    }

    /**
     * Get if every component was applied.
     *
     * @return If there were no errors.
     */
    public boolean isSuccessful() {
        return this.errors.isEmpty();
    }
}
