package com.willfp.eco.core.gui.slot;

import com.willfp.eco.core.Eco;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Function;

public interface Slot {
    /**
     * Get the ItemStack that would be shown to a player.
     *
     * @param player The player.
     * @return The ItemStack.
     */
    ItemStack getItemStack(@NotNull Player player);

    /**
     * Create a builder for an ItemStack.
     *
     * @param itemStack The ItemStack.
     * @return The builder.
     */
    static Builder builder(@NotNull final ItemStack itemStack) {
        return Eco.getHandler().getGUIFactory().createSlotBuilder(player -> itemStack);
    }

    /**
     * Create a builder for a player-specific ItemStack.
     *
     * @param provider The provider.
     * @return The builder.
     */
    static Builder builder(@NotNull final Function<Player, ItemStack> provider) {
        return Eco.getHandler().getGUIFactory().createSlotBuilder(provider);
    }

    interface Builder {
        Builder onLeftClick(@NotNull BiConsumer<InventoryClickEvent, Slot> action);

        Builder onRightClick(@NotNull BiConsumer<InventoryClickEvent, Slot> action);

        Builder onShiftLeftClick(@NotNull BiConsumer<InventoryClickEvent, Slot> action);

        Builder onShiftRightClick(@NotNull BiConsumer<InventoryClickEvent, Slot> action);

        Builder onMiddleClick(@NotNull BiConsumer<InventoryClickEvent, Slot> action);

        Slot build();
    }
}
