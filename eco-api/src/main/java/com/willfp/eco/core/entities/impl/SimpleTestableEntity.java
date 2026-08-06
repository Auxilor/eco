package com.willfp.eco.core.entities.impl;

import com.google.common.base.Preconditions;
import com.willfp.eco.core.entities.TestableEntity;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A vanilla entity, matched and spawned purely by its {@link EntityType}.
 */
public class SimpleTestableEntity implements TestableEntity {
    /**
     * The entity type.
     */
    private final EntityType type;

    /**
     * Create a new simple testable entity.
     *
     * @param type The entity type, which must have a known entity class.
     * @throws NullPointerException If the type has no entity class.
     */
    public SimpleTestableEntity(@NotNull final EntityType type) {
        this.type = type;

        Preconditions.checkNotNull(type.getEntityClass(), "Entity cannot be of unknown type!");
    }

    @Override
    public boolean matches(@Nullable final Entity entity) {
        return entity != null && entity.getType() == type;
    }

    @Override
    public Entity spawn(@NotNull final Location location) {
        Preconditions.checkNotNull(location.getWorld(), "World must not be null!");

        assert type.getEntityClass() != null;

        return location.getWorld().spawn(location, type.getEntityClass());
    }

    /**
     * Get the entity type.
     *
     * @return The entity type.
     */
    public EntityType getType() {
        return this.type;
    }
}
