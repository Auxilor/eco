package com.willfp.eco.core.entities.tag;

import org.bukkit.Tag;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

/**
 * A vanilla entity tag.
 */
public final class VanillaEntityTag implements EntityTag {
    /**
     * The identifier.
     */
    private final String identifier;

    /**
     * The tag.
     */
    private final Tag<EntityType> tag;

    /**
     * Create a new vanilla entity tag.
     *
     * @param identifier The identifier.
     * @param tag        The tag.
     */
    public VanillaEntityTag(@NotNull final String identifier,
                            @NotNull final Tag<EntityType> tag) {
        this.identifier = identifier;
        this.tag = tag;
    }

    /**
     * Get the tag.
     *
     * @return The tag.
     */
    public Tag<EntityType> getTag() {
        return tag;
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public boolean matches(@NotNull final Entity entity) {
        return tag.isTagged(entity.getType());
    }
}