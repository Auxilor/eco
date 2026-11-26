package com.willfp.eco.core.display;

import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Records what display changed on an item, so it can be reverted exactly.
 */
@ApiStatus.Internal
public interface DisplayRecorder {
    /**
     * Get an item backed by a server item, so every change to it is made in one place.
     *
     * @param itemStack The item.
     * @return The same item if it is already backed by a server item, otherwise a copy that is.
     */
    @NotNull
    ItemStack mirror(@NotNull ItemStack itemStack);

    /**
     * Get an object that is replaced whenever the lore on an item is set, to find out cheaply if
     * anything set it.
     *
     * @param itemStack The item.
     * @return The lore state, or null if the item has no lore.
     */
    @Nullable
    Object getLoreState(@NotNull ItemStack itemStack);

    /**
     * Get the lore a lore state stands for. It is the same whenever this is called, whatever
     * happened to the item since the state was taken.
     *
     * @param loreState The lore state, from {@link #getLoreState(ItemStack)}.
     * @return The lore, empty if the state is null.
     */
    @NotNull
    List<Component> getLore(@Nullable Object loreState);

    /**
     * If any lore line on an item may be a display line, checked without converting the lore
     * to components. If this returns false, {@link Display#isDisplayLine(Component)} is false for
     * every line on the item.
     *
     * @param itemStack The item.
     * @return If there may be display lines.
     */
    default boolean mayHaveDisplayLines(@NotNull final ItemStack itemStack) {
        return true;
    }

    /**
     * Set the lore on an item. Display lines are styled as eco has always shown them, and every
     * other line is set exactly as given.
     *
     * @param itemStack The item.
     * @param lore      The lore.
     */
    void setLore(@NotNull ItemStack itemStack,
                 @NotNull List<Component> lore);

    /**
     * Write a record of what display changed since a snapshot onto the item.
     * <p>
     * No record is written if display changed nothing. No record is written either if display
     * changed the item's type or amount, so the item is reverted by stripping display lines and
     * running module reverts instead. The same applies if the record would be larger than the
     * maximum record size.
     *
     * @param itemStack    The displayed item.
     * @param snapshot     A copy of the item taken before display.
     * @param displayLines The positions of display lines in the lore.
     */
    void record(@NotNull ItemStack itemStack,
                @NotNull ItemStack snapshot,
                @NotNull int[] displayLines);

    /**
     * Restore an item from its record, if it has a valid one. Records written onto an item that
     * was already displayed are restored too, down to the item before any display.
     * <p>
     * Records are signed and encrypted with keys held only by this server, so a client can't
     * forge one or read the original values in it. A record that is not valid is removed from
     * the item without restoring anything.
     *
     * @param itemStack The item.
     * @return If the item was restored.
     */
    boolean restore(@NotNull ItemStack itemStack);
}
