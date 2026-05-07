package com.willfp.eco.core.blocks.args;

import com.willfp.eco.core.entities.args.EntityArgParser;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

/**
 * The result of an arg parses.
 *
 * @param test     The test for the block.
 * @param modifier The modifier to apply to the block.
 * @see EntityArgParser
 */
public record BlockArgParseResult(@NotNull Predicate<Block> test,
                                  @NotNull Consumer<Block> modifier) {
    /**
     * Kotlin destructuring support.
     *
     * @return The test.
     */
    public Predicate<Block> component1() {
        return test;
    }

    /**
     * Kotlin destructuring support.
     *
     * @return The modifier.
     */
    public Consumer<Block> component2() {
        return modifier;
    }
}
