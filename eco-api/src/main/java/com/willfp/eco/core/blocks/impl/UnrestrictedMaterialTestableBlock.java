package com.willfp.eco.core.blocks.impl;

import com.willfp.eco.core.blocks.TestableBlock;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A testable block for materials regardless of data.
 */
public class UnrestrictedMaterialTestableBlock implements TestableBlock {
    /**
     * The block type.
     */
    private final Material material;

    /**
     * Create a new unrestricted material testable block.
     *
     * @param material The material.
     */
    public UnrestrictedMaterialTestableBlock(@NotNull final Material material) {
        this.material = material;
    }

    @Override
    public boolean matches(@Nullable final Block other) {
        return other != null && other.getType() == material;
    }

    @Override
    public @NotNull Block place(@NotNull Location location) {
        Validate.notNull(location.getWorld());

        Block block = location.getWorld().getBlockAt(location);
        block.setType(material);

        return block;
    }

    /**
     * Get the material.
     *
     * @return The material.
     */
    public Material getMaterial() {
        return this.material;
    }
}
