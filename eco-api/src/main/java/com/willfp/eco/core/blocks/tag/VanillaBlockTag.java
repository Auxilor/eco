package com.willfp.eco.core.blocks.tag;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * A vanilla block tag.
 */
public final class VanillaBlockTag implements BlockTag {
    /**
     * The identifier.
     */
    private final String identifier;

    /**
     * The tag.
     */
    private final Tag<Material> tag;

    /**
     * Create a new vanilla block tag.
     *
     * @param identifier The identifier.
     * @param tag        The tag.
     */
    public VanillaBlockTag(@NotNull final String identifier,
                           @NotNull final Tag<Material> tag) {
        this.identifier = identifier;
        this.tag = tag;
    }

    /**
     * Get the tag.
     *
     * @return The tag.
     */
    public Tag<Material> getTag() {
        return tag;
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public boolean matches(@NotNull final Block block) {
        return tag.isTagged(block.getType());
    }
}
