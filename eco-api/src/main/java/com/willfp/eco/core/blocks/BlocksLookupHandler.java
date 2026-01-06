package com.willfp.eco.core.blocks;

import com.willfp.eco.core.blocks.impl.EmptyTestableBlock;
import com.willfp.eco.core.blocks.impl.GroupedTestableBlocks;
import com.willfp.eco.core.lookup.LookupHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Function;

/**
 * Handle block lookup strings.
 */
public class BlocksLookupHandler implements LookupHandler<TestableBlock> {
    /**
     * The parser.
     */
    private final Function<String[], @NotNull TestableBlock> parser;

    /**
     * Create new lookup handler.
     *
     * @param parser The parser.
     */
    public BlocksLookupHandler(@NotNull final Function<String[], @NotNull TestableBlock> parser) {
        this.parser = parser;
    }

    @Override
    public @NotNull TestableBlock parse(@NotNull final String[] args) {
        return parser.apply(args);
    }

    @Override
    public boolean validate(@NotNull final TestableBlock object) {
        return !(object instanceof EmptyTestableBlock);
    }

    @Override
    public @NotNull TestableBlock getFailsafe() {
        return new EmptyTestableBlock();
    }

    @Override
    public @NotNull TestableBlock join(@NotNull final Collection<TestableBlock> options) {
        return new GroupedTestableBlocks(options);
    }
}
