package com.willfp.eco.core.display;

import com.willfp.eco.core.placeholder.context.PlaceholderContext;
import java.util.Collections;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The display context eco passes to modules.
 *
 * @param itemStack  The item.
 * @param player     The player, or null for no player context.
 * @param properties The display properties.
 * @param varArgs    The module's varargs.
 * @param lore       The lore.
 */
record EcoDisplayContext(@NotNull ItemStack itemStack,
                         @Nullable Player player,
                         @NotNull DisplayProperties properties,
                         @NotNull Object[] varArgs,
                         @NotNull DisplayLore lore) implements DisplayContext {
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
