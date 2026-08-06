package com.willfp.eco.core.gui.slot;

import com.willfp.eco.core.items.Items;
import com.willfp.eco.core.items.TestableItem;
import com.willfp.eco.core.recipe.parts.EmptyTestableItem;
import com.willfp.eco.core.recipe.parts.MaterialTestableItem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

/**
 * Mask items store a set of items which can be accessed by
 * a {@link FillerMask}.
 *
 * @param items The items.
 */
public record MaskItems(@NotNull TestableItem... items) {
    /**
     * Create mask items from materials.
     *
     * @param materials The materials.
     */
    public MaskItems(@NotNull final Material... materials) {
        this(Arrays.stream(materials).map(MaterialTestableItem::new).toList().toArray(new TestableItem[0]));
    }

    /**
     * Create {@link MaskItems} from a list of item names.
     * <p>
     * Names that do not resolve to an item are skipped. If no names resolve,
     * the mask items will contain a single black stained glass pane.
     *
     * @param names The names.
     * @return The mask items.
     */
    public static MaskItems fromItemNames(@NotNull final Iterable<String> names) {
        List<TestableItem> items = new ArrayList<>();

        for (String name : names) {
            TestableItem item = Items.lookup(name);

            if (item instanceof EmptyTestableItem) {
                continue;
            }

            items.add(item);
        }

        if (items.isEmpty()) {
            return new MaskItems(new MaterialTestableItem(Material.BLACK_STAINED_GLASS_PANE));
        }

        return new MaskItems(items.toArray(new TestableItem[0]));
    }
}
