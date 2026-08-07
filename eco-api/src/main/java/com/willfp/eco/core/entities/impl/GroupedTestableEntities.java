package com.willfp.eco.core.entities.impl;

import com.google.common.base.Preconditions;
import com.willfp.eco.core.entities.TestableEntity;
import com.willfp.eco.util.NumberUtils;
import java.util.ArrayList;
import java.util.Collection;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A group of testable entities, matching if any child matches, and spawning a random child.
 *
 * @see com.willfp.eco.core.entities.CustomEntity
 */
public class GroupedTestableEntities implements TestableEntity {
    /**
     * The children.
     */
    private final Collection<TestableEntity> children;

    /**
     * Create a new group of testable entities.
     *
     * @param children The children, which must not be empty.
     * @throws IllegalArgumentException If the children are empty.
     */
    public GroupedTestableEntities(@NotNull final Collection<TestableEntity> children) {
        Preconditions.checkArgument(!children.isEmpty(), "Group must have at least one child!");

        this.children = children;
    }

    /**
     * If the entity matches any children.
     *
     * @param entity The entity to test, which may be null.
     * @return If the entity matches the test of any children.
     */
    @Override
    public boolean matches(@Nullable final Entity entity) {
        for (TestableEntity child : children) {
            if (child.matches(entity)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Entity spawn(@NotNull final Location location) {
        return new ArrayList<>(children)
                .get(NumberUtils.randInt(0, children.size() - 1))
                .spawn(location);
    }

    /**
     * Get the children.
     *
     * @return A copy of the children.
     */
    public Collection<TestableEntity> getChildren() {
        return new ArrayList<>(children);
    }
}
