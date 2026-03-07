package com.willfp.eco.core.entities;

import com.google.common.base.Preconditions;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A custom entity has 3 components.
 *
 * <ul>
 *     <li>The key to identify it</li>
 *     <li>The test to check if any entity is this custom entity</li>
 *     <li>The supplier to spawn the custom {@link org.bukkit.entity.Entity}</li>
 * </ul>
 */
public class CustomEntity implements TestableEntity {
    /**
     * The key.
     */
    private final NamespacedKey key;

    /**
     * The test for Entities to pass.
     */
    private final Predicate<@NotNull Entity> test;

    /**
     * The provider to spawn the entity.
     */
    private final Function<Location, Entity> provider;

    /**
     * Create a new custom entity.
     *
     * @param key      The entity key.
     * @param test     The test.
     * @param provider The provider to spawn the entity.
     */
    public CustomEntity(@NotNull final NamespacedKey key,
                        @NotNull final Predicate<@NotNull Entity> test,
                        @NotNull final Function<Location, Entity> provider) {
        this.key = key;
        this.test = test;
        this.provider = provider;
    }

    @Override
    public boolean matches(@Nullable final Entity entity) {
        if (entity == null) {
            return false;
        }

        return test.test(entity);
    }

    @Override
    public Entity spawn(@NotNull final Location location) {
        Preconditions.checkNotNull(location.getWorld(), "World must not be null!");

        return provider.apply(location);
    }

    /**
     * Register the entity.
     */
    public void register() {
        Entities.registerCustomEntity(this.getKey(), this);
    }

    /**
     * Get the key.
     *
     * @return The key.
     */
    public NamespacedKey getKey() {
        return this.key;
    }
}
