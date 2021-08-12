package com.willfp.eco.core.gui.slot;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.gui.slot.functional.SlotProvider;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * A slot is an item in a GUI that can handle clicks.
 */
public interface Slot {
    /**
     * Get the ItemStack that would be shown to a player.
     *
     * @param player The player.
     * @return The ItemStack.
     */
    ItemStack getItemStack(@NotNull Player player);

    /**
     * If the slot is captive. (Can items be placed in it).
     *
     * @return If captive.
     */
    boolean isCaptive();

    /**
     * Create a builder for an ItemStack.
     *
     * @return The builder.
     */
    static SlotBuilder builder() {
        return Eco.getHandler().getGUIFactory().createSlotBuilder((player, menu) -> new ItemStack(Material.AIR));
    }

    /**
     * Create a builder for an ItemStack.
     *
     * @param itemStack The ItemStack.
     * @return The builder.
     */
    static SlotBuilder builder(@NotNull final ItemStack itemStack) {
        return Eco.getHandler().getGUIFactory().createSlotBuilder((player, menu) -> itemStack);
    }

    /**
     * Create a builder for a player-specific ItemStack.
     *
     * @param provider The provider.
     * @return The builder.
     */
    static SlotBuilder builder(@NotNull final Function<Player, ItemStack> provider) {
        return Eco.getHandler().getGUIFactory().createSlotBuilder((player, menu) -> provider.apply(player));
    }

    /**
     * Create a builder for a player-specific ItemStack.
     *
     * @param provider The provider.
     * @return The builder.
     */
    static SlotBuilder builder(@NotNull final SlotProvider provider) {
        return Eco.getHandler().getGUIFactory().createSlotBuilder(provider);
    }
}
