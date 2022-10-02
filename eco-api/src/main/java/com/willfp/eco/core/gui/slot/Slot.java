package com.willfp.eco.core.gui.slot;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.gui.GUIComponent;
import com.willfp.eco.core.gui.menu.Menu;
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
 * While you can create custom Slot implementations directly from this class,
 * it's heavily encouraged to extend {@link CustomSlot}, which will abstract
 * internal functionality away.
 * <p>
 * A lot of methods here are marked as default as in 6.43.0 the GUI system
 * was overhauled, but to preserve backwards compatibility, the new methods
 * had to be marked default, and many old methods became deprecated.
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
     * @param player The player.
     * @param menu   The menu.
     * @return If captive.
     */
    default boolean isCaptive(@NotNull final Player player,
                              @NotNull final Menu menu) {
        return false;
    }

    /**
     * Get the actionable slot to be shown.
     * <p>
     * This is mostly internal, if you want to implement custom slots you should
     * turn to {@link CustomSlot} or {@link ReactiveSlot}, which abstract this
     * behaviour away.
     * <p>
     * **Never** return {@code this} from this method. Always make sure that your
     * slots eventually delegate to a slot created by {@link Slot#builder()}.
     * <p>
     * {@code this} is returned by default for backwards-compatibility.
     *
     * @param player The player.
     * @param menu   The menu.
     * @return The slot.
     */
    default Slot getActionableSlot(@NotNull final Player player,
                                   @NotNull final Menu menu) {
        return this;
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
        return Eco.get().createSlotBuilder((player, menu) -> new ItemStack(Material.AIR));
    }

    /**
     * Create a builder for an ItemStack.
     *
     * @param itemStack The ItemStack.
     * @return The builder.
     */
    static SlotBuilder builder(@NotNull final ItemStack itemStack) {
        return Eco.get().createSlotBuilder((player, menu) -> itemStack);
    }

    /**
     * Create a builder for a TestableItem.
     *
     * @param item The item.
     * @return The builder.
     */
    static SlotBuilder builder(@NotNull final TestableItem item) {
        return Eco.get().createSlotBuilder((player, menu) -> item.getItem());
    }

    /**
     * Create a builder for a player-specific ItemStack.
     *
     * @param provider The provider.
     * @return The builder.
     */
    static SlotBuilder builder(@NotNull final Function<Player, ItemStack> provider) {
        return Eco.get().createSlotBuilder((player, menu) -> provider.apply(player));
    }

    /**
     * Create a builder for a player-specific ItemStack.
     *
     * @param provider The provider.
     * @return The builder.
     */
    static SlotBuilder builder(@NotNull final SlotProvider provider) {
        return Eco.get().createSlotBuilder(provider);
    }

    /**
     * If the slot is not captive for a player.
     *
     * @param player The player.
     * @return If not captive for the player.
     * @deprecated Captivity is now reactive, this method can produce incorrect results.
     */
    @Deprecated(since = "6.43.0", forRemoval = true)
    default boolean isNotCaptiveFor(@NotNull Player player) {
        return false;
    }

    /**
     * If the slot is captive. (Can items be placed in it).
     *
     * @return If captive.
     * @deprecated Captivity is now reactive, this method can produce incorrect results.
     */
    @Deprecated(since = "6.43.0", forRemoval = true)
    default boolean isCaptive() {
        return false;
    }
}
