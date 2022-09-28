package com.willfp.eco.core.gui.slot;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.gui.component.GUIComponent;
import com.willfp.eco.core.gui.slot.functional.SlotProvider;
import com.willfp.eco.core.items.TestableItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * A slot is an item in a GUI that can handle clicks.
 * <p>
 * Don't create custom Slot implementations directly from this class,
 * rather extend {@link CustomSlot}.
 */
public interface Slot extends GUIComponent {
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
     * If the slot is not captive for a player.
     *
     * @param player The player.
     * @return If not captive for the player.
     */
    default boolean isNotCaptiveFor(@NotNull Player player) {
        return false;
    }

    /**
     * If the slot is captive from empty.
     * If true, a captive item will be returned even if the item is the same as the rendered item.
     *
     * @return If captive from empty.
     */
    default boolean isCaptiveFromEmpty() {
        return false;
    }

    @Override
    default int getRows() {
        return 1;
    }

    @Override
    default int getColumns() {
        return 1;
    }

    @Override
    default Slot getSlotAt(final int row,
                           final int column) {
        return this;
    }

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
     * Create a builder for a TestableItem.
     *
     * @param item The item.
     * @return The builder.
     */
    static SlotBuilder builder(@NotNull final TestableItem item) {
        return Eco.getHandler().getGUIFactory().createSlotBuilder((player, menu) -> item.getItem());
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
