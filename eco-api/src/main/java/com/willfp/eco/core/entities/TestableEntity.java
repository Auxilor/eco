package com.willfp.eco.core.entities;

import com.willfp.eco.core.lookup.Testable;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An item with a test to see if any item is that item.
 */
public interface TestableEntity extends Testable<Entity> {
    /**
     * If an Entity matches the test.
     *
     * @param entity The entity to test.
     * @return If the entity matches.
     */
    @Override
    boolean matches(@Nullable Entity entity);

    /**
     * Spawn the entity.
     *
     * @param location The location.
     * @return The entity.
     */
    Entity spawn(@NotNull Location location);
}
