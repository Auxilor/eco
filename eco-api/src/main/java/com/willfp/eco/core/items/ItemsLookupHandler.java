package com.willfp.eco.core.items;

import com.willfp.eco.core.lookup.LookupHandler;
import com.willfp.eco.core.recipe.parts.EmptyTestableItem;
import com.willfp.eco.core.recipe.parts.GroupedTestableItems;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Function;

/**
 * Handle item lookup strings.
 */
public class ItemsLookupHandler implements LookupHandler<TestableItem> {
    /**
     * The parser.
     */
    private final Function<String[], @NotNull TestableItem> parser;

    /**
     * Create new lookup handler.
     *
     * @param parser The parser.
     */
    public ItemsLookupHandler(@NotNull final Function<String[], @NotNull TestableItem> parser) {
        this.parser = parser;
    }

    @Override
    public @NotNull TestableItem parse(@NotNull final String[] args) {
        return parser.apply(args);
    }

    @Override
    public boolean validate(@NotNull final TestableItem object) {
        return !(object instanceof EmptyTestableItem);
    }

    @Override
    public @NotNull TestableItem getFailsafe() {
        return new EmptyTestableItem();
    }

    @Override
    public @NotNull TestableItem join(@NotNull final Collection<TestableItem> options) {
        return new GroupedTestableItems(options);
    }
}
