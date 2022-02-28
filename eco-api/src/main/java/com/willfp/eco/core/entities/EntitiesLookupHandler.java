package com.willfp.eco.core.entities;

import com.willfp.eco.core.entities.impl.EmptyTestableEntity;
import com.willfp.eco.core.entities.impl.GroupedTestableEntities;
import com.willfp.eco.core.lookup.LookupHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Function;

/**
 * Handle item lookup strings.
 */
public class EntitiesLookupHandler implements LookupHandler<TestableEntity> {
    /**
     * The parser.
     */
    private final Function<String[], @NotNull TestableEntity> parser;

    /**
     * Create new lookup handler.
     *
     * @param parser The parser.
     */
    public EntitiesLookupHandler(@NotNull final Function<String[], @NotNull TestableEntity> parser) {
        this.parser = parser;
    }

    @Override
    public @NotNull TestableEntity parse(@NotNull final String[] args) {
        return parser.apply(args);
    }

    @Override
    public boolean validate(@NotNull final TestableEntity object) {
        return !(object instanceof EmptyTestableEntity);
    }

    @Override
    public @NotNull TestableEntity getFailsafe() {
        return new EmptyTestableEntity();
    }

    @Override
    public @NotNull TestableEntity join(@NotNull final Collection<TestableEntity> options) {
        return new GroupedTestableEntities(options);
    }
}
