package com.willfp.eco.core.gui.slot;

import com.willfp.eco.core.gui.GUIComponent;
import com.willfp.eco.core.items.builder.ItemStackBuilder;
import com.willfp.eco.core.recipe.parts.EmptyTestableItem;
import com.willfp.eco.core.recipe.parts.MaterialTestableItem;
import com.willfp.eco.util.ListUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

/**
 * Mask of filler slots.
 * <p>
 * A pattern consists of 1s and 0s, where a 1 is a filler slot,
 * and a 0 isn't.
 * <p>
 * For example, creating a filler mask for a single-chest sized menu would look like this:
 * <p>
 * new FillerMask(
 * material,
 * "11111111"
 * "10000001"
 * "11111111"
 * );
 */
public class FillerMask implements GUIComponent {
    /**
     * Mask.
     */
    private final List<List<Slot>> mask;

    /**
     * Rows.
     */
    private final int rows;

    /**
     * Create a new filler mask.
     *
     * @param material The mask material.
     * @param pattern  The pattern.
     */
    public FillerMask(@NotNull final Material material,
                      @NotNull final String... pattern) {
        this(new MaskItems(new MaterialTestableItem(material)), pattern);
    }

    /**
     * Create a new filler mask.
     *
     * @param materials The mask materials.
     * @param pattern   The pattern.
     * @deprecated Use {@link MaskItems} instead.
     */
    @Deprecated(since = "6.24.0")
    public FillerMask(@NotNull final MaskMaterials materials,
                      @NotNull final String... pattern) {
        this(
                materials.toMaskItems(),
                pattern
        );
    }

    /**
     * Create a new filler mask.
     *
     * @param items   The mask items.
     * @param pattern The pattern.
     */
    public FillerMask(@NotNull final MaskItems items,
                      @NotNull final String... pattern) {
        if (Arrays.stream(items.items()).anyMatch(item -> item instanceof EmptyTestableItem)) {
            throw new IllegalArgumentException("Items cannot be empty!");
        }

        rows = pattern.length;
        mask = ListUtils.create2DList(rows, 9);

        for (int i = 0; i < items.items().length; i++) {
            ItemStack itemStack = new ItemStackBuilder(items.items()[i])
                    .setDisplayName("&r")
                    .build();

            int row = 0;

            for (String patternRow : pattern) {
                int column = 0;
                for (char c : patternRow.toCharArray()) {
                    if (c == '0') {
                        mask.get(row).set(column, null);
                    } else if (c == Character.forDigit(i + 1, 10)) {
                        mask.get(row).set(column, new FillerSlot(itemStack));
                    }

                    column++;
                }
                row++;
            }
        }
    }

    /**
     * Get the mask.
     *
     * @return The mask.
     */
    public List<List<Slot>> getMask() {
        return this.mask;
    }

    @Override
    public int getRows() {
        return rows;
    }

    @Override
    public int getColumns() {
        return 9;
    }

    @Override
    public @Nullable Slot getSlotAt(final int row,
                                    final int column) {
        return mask.get(row - 1).get(column - 1);
    }
}
