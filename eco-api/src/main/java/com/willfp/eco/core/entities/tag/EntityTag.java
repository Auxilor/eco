package com.willfp.eco.core.entities.tag;

import com.willfp.eco.core.entities.TestableEntity;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

/**
 * A group of entities that share a common trait.
 */
public interface EntityTag {
    /**
     * Get the identifier of the tag.
     *
     * @return The identifier.
     */
    @NotNull
    String getIdentifier();

    /**
     * Check if an entity matches the tag.
     *
     * @param entity The entity to check.
     * @return If the entity matches the tag.
     */
    boolean matches(@NotNull Entity entity);

    /**
     * Convert this tag to a testable entity.
     *
     * @return The testable entity.
     */
    @NotNull
    default TestableEntity toTestableEntity() {
        return new TestableEntity() {
            @Override
            public boolean matches(@org.jetbrains.annotations.Nullable final Entity entity) {
                return entity != null && EntityTag.this.matches(entity);
            }

            @Override
            public @NotNull Entity spawn(@NotNull final org.bukkit.Location location) {
                throw new UnsupportedOperationException("Cannot spawn a tag-based entity");
            }

            @Override
            public String toString() {
                return "EntityTagTestableEntity{" +
                        "tag=" + EntityTag.this.getIdentifier() +
                        '}';
            }
        };
    }
}