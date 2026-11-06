package com.willfp.eco.core.items.provider;

import com.willfp.eco.core.items.TestableItem;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Item providers are call-site registrations for items. In other words,
 * they only register their items when a request is made. This is marginally
 * slower, however it is required for certain plugins, and fixes bugs related to
 * loading orders.
 *
 * @see TestableItem
 */
public abstract class ItemProvider {
    /**
     * The namespace.
     */
    private final String namespace;

    /**
     * Create a new ItemProvider for a specific namespace.
     *
     * @param namespace The namespace.
     */
    protected ItemProvider(@NotNull final String namespace) {
        this.namespace = namespace;
    }

    /**
     * Provide a TestableItem for a given key.
     *
     * @param key The item ID.
     * @return The TestableItem, or null if not found.
     */
    @Nullable
    public abstract TestableItem provideForKey(@NotNull String key);

    /**
     * Get the item ID of an ItemStack, the reverse of {@link #provideForKey(String)}.
     * <p>
     * Providers only register their items when a request is made, so this is what lets
     * an item be recognised before anything has looked it up.
     *
     * @param itemStack The ItemStack.
     * @return The item ID, or null if the ItemStack isn't from this provider.
     */
    @Nullable
    public String getKey(@NotNull final ItemStack itemStack) {
        return null;
    }

    /**
     * Get the namespace.
     *
     * @return The namespace.
     */
    public String getNamespace() {
        return this.namespace;
    }
}
