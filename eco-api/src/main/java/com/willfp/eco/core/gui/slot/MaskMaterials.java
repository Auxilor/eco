package com.willfp.eco.core.gui.slot;

import com.willfp.eco.core.items.Items;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

/**
 * Mask materials store a set of materials which can be accessed by
 * a filler mask.
 *
 * @param materials The materials.
 * @deprecated Use {@link MaskItems} instead.
 */
@Deprecated(since = "6.24.0")
public record MaskMaterials(@NotNull Material... materials) {
    /**
     * Convert MaskMaterials to MaskItems.
     *
     * @return The MaskItems.
     */
    public MaskItems toMaskItems() {
        return new MaskItems(Items.fromMaterials(this.materials()));
    }
}
