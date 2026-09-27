package com.willfp.eco.core.display;

import com.willfp.eco.core.placeholder.context.PlaceholderContext;
import java.util.Collections;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The display context eco passes to modules.
 */
final class EcoDisplayContext implements DisplayContext {
    /**
     * The item.
     */
    private final ItemStack itemStack;

    /**
     * The player.
     */
    private final Player player;

    /**
     * The display properties.
     */
    private final DisplayProperties properties;

    /**
     * The module's varargs.
     */
    private final Object[] varArgs;

    /**
     * The lore.
     */
    private final DisplayLore lore;

    /**
     * Create a display context.
     *
     * @param itemStack  The item.
     * @param player     The player, or null for no player context.
     * @param properties The display properties.
     * @param varArgs    The module's varargs.
     * @param lore       The lore.
     */
    EcoDisplayContext(@NotNull final ItemStack itemStack,
                      @Nullable final Player player,
                      @NotNull final DisplayProperties properties,
                      @NotNull final Object[] varArgs,
                      @NotNull final DisplayLore lore) {
        this.itemStack = itemStack;
        this.player = player;
        this.properties = properties;
        this.varArgs = varArgs;
        this.lore = lore;
    }

    @Override
    @NotNull
    public ItemStack getItemStack() {
        return this.itemStack;
    }

    @Override
    @Nullable
    public Player getPlayer() {
        return this.player;
    }

    @Override
    @NotNull
    public DisplayProperties getProperties() {
        return this.properties;
    }

    @Override
    @NotNull
    public Object[] getVarArgs() {
        return this.varArgs;
    }

    @Override
    @NotNull
    public PlaceholderContext getPlaceholderContext() {
        return new PlaceholderContext(this.player, this.itemStack, null, Collections.emptyList());
    }

    @Override
    @NotNull
    public DisplayLore getLore() {
        return this.lore;
    }
}
