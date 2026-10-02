package com.willfp.eco.core.display;

import com.willfp.eco.core.placeholder.context.PlaceholderContext;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Everything a display module needs to display an item.
 * <p>
 * Changes to anything other than lore are made on {@link #getItemStack()} directly, and are
 * reverted exactly when the item comes back from the client.
 */
public interface DisplayContext {
    /**
     * The item being displayed. This is the copy sent to the client.
     *
     * @return The item.
     */
    @NotNull
    ItemStack getItemStack();

    /**
     * The player the item is displayed for.
     *
     * @return The player, or null for no player context.
     */
    @Nullable
    Player getPlayer();

    /**
     * The display properties.
     *
     * @return The properties.
     */
    @NotNull
    DisplayProperties getProperties();

    /**
     * The varargs this module generated for the item.
     *
     * @return The varargs.
     */
    @NotNull
    Object[] getVarArgs();

    /**
     * A placeholder context for the player and item.
     *
     * @return The context.
     */
    @NotNull
    PlaceholderContext getPlaceholderContext();

    /**
     * The lore.
     *
     * @return The lore.
     */
    @NotNull
    DisplayLore getLore();
}
