package com.willfp.eco.core.gui.slot;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

/**
 * Mask materials store a set of materials which can be accessed by
 * a filler mask.
 *
 * @param materials The materials.
 */
public record MaskMaterials(@NotNull Material... materials) {

}
