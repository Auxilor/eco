package com.willfp.eco.util;

import lombok.experimental.UtilityClass;
import org.bukkit.entity.Arrow;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Utilities / API methods for arrows.
 */
@UtilityClass
public class ArrowUtils {

    /**
     * Get the bow from an arrow.
     *
     * @param arrow The arrow.
     * @return The bow.
     */
    @Nullable
    public ItemStack getBow(@NotNull final Arrow arrow) {
        List<MetadataValue> values = arrow.getMetadata("shot-from");

        if (values.isEmpty()) {
            return null;
        }

        if (!(values.get(0).value() instanceof ItemStack)) {
            return null;
        }

        return (ItemStack) values.get(0).value();
    }
}
