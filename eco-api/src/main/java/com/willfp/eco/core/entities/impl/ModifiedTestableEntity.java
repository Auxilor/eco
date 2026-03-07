package com.willfp.eco.core.entities.impl;

import com.google.common.base.Preconditions;
import com.willfp.eco.core.entities.TestableEntity;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Existing testable entity with an extra filter.
 *
 * @see com.willfp.eco.core.entities.CustomEntity
 */
public class ModifiedTestableEntity implements TestableEntity {
    /**
     * The item.
     */
    private final TestableEntity handle;

    /**
     * The amount.
     */
    private final Predicate<Entity> test;

    /**
     * The provider to spawn the entity.
     */
    private final Function<Location, Entity> provider;

    /**
     * Create a new modified testable entity.
     *
     * @param entity   The base entity.
     * @param test     The test.
     * @param provider The provider to spawn the entity.
     */
    public ModifiedTestableEntity(@NotNull final TestableEntity entity,
                                  @NotNull final Predicate<@NotNull Entity> test,
                                  @NotNull final Function<Location, Entity> provider) {
        this.handle = entity;
        this.test = test;
        this.provider = provider;
    }

    @Override
    public boolean matches(@Nullable final Entity entity) {
        return entity != null && handle.matches(entity) && test.test(entity);
    }

    @Override
    public Entity spawn(@NotNull final Location location) {
        Preconditions.checkNotNull(location.getWorld(), "World must not be null!");

        return provider.apply(location);
    }

    /**
     * Get the handle.
     *
     * @return The handle.
     */
    public TestableEntity getHandle() {
        return this.handle;
    }
}
