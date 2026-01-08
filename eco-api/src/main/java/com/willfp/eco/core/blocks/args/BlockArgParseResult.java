package com.willfp.eco.core.blocks.args;

import com.willfp.eco.core.entities.args.EntityArgParser;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * The result of an arg parses.
 *
 * @param test     The test for the entity.
 * @param modifier The modifier to apply to the entity.
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
