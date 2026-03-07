package com.willfp.eco.util;

import org.bukkit.entity.Arrow;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Utilities / API methods for arrows.
 */
public final class ArrowUtils {
    /**
     * Get the bow from an arrow.
     *
     * @param arrow The arrow.
     * @return The bow, or null if no bow.
     */
    @Nullable
    public static ItemStack getBow(@NotNull final Arrow arrow) {
        List<MetadataValue> values = arrow.getMetadata("shot-from");

        if (values.isEmpty()) {
            return null;
        }

        if (!(values.getFirst().value() instanceof ItemStack)) {
            return null;
        }

        return (ItemStack) values.getFirst().value();
    }

    private ArrowUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
