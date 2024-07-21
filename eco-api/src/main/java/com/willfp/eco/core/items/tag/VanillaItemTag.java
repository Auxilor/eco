package com.willfp.eco.core.items.tag;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * A vanilla item tag.
 */
public final class VanillaItemTag implements ItemTag {
    /**
     * The identifier.
     */
    private final String identifier;

    /**
     * The tag.
     */
    private final Tag<Material> tag;

    /**
     * Create a new vanilla item tag.
     *
     * @param identifier The identifier.
     * @param tag        The tag.
     */
    public VanillaItemTag(@NotNull final String identifier,
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
    public boolean matches(@NotNull final ItemStack itemStack) {
        return tag.isTagged(itemStack.getType());
    }

    @Override
    public @NotNull ItemStack getExampleItem() {
        return new ItemStack(tag.getValues().stream().findFirst().orElse(Material.STONE));
    }
}
