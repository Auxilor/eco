package com.willfp.eco.core.entities.impl;

import com.google.common.base.Preconditions;
import com.willfp.eco.core.Eco;
import com.willfp.eco.core.entities.TestableEntity;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An empty entity: matches nothing, and spawns a {@link com.willfp.eco.core.entities.DummyEntity}.
 * <p>
 * Used as the failsafe result of a failed entity lookup.
 */
public class EmptyTestableEntity implements TestableEntity {
    /**
     * Create a new empty testable entity.
     */
    public EmptyTestableEntity() {

    }

    @Override
    public boolean matches(@Nullable final Entity entity) {
        return false;
    }

    @Override
    public Entity spawn(@NotNull final Location location) {
        Preconditions.checkNotNull(location.getWorld(), "World must not be null!");

        return Eco.get().createDummyEntity(location);
    }
}
