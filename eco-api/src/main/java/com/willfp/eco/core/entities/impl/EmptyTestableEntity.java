package com.willfp.eco.core.entities.impl;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.entities.TestableEntity;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Empty entity.
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
        Validate.notNull(location.getWorld());

        return Eco.get().createDummyEntity(location);
    }
}
