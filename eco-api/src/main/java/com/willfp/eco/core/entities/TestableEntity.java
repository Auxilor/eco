package com.willfp.eco.core.entities;

import com.willfp.eco.core.lookup.Testable;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An entity with a test to see if any entity is that entity.
 */
public interface TestableEntity extends Testable<Entity> {
    /**
     * If an entity matches the test.
     *
     * @param entity The entity to test, which may be null.
     * @return If the entity matches.
     */
    @Override
    boolean matches(@Nullable Entity entity);

    /**
     * Spawn the entity.
     *
     * @param location The location to spawn the entity at.
     * @return The spawned entity.
     */
    Entity spawn(@NotNull Location location);
}
