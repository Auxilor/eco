package com.willfp.eco.core.blocks.args;

import com.willfp.eco.core.blocks.TestableBlock;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * An argument parser that should generate
 * a modified BlockData for {@link TestableBlock#place(Location)} .
 */
public interface BlockArgParser {
    /**
     * Parse the arguments.
     *
     * @param args      The arguments.
     * @param blockData The block data to be modified.
     * @return The modified block data.
     */
    @Nullable BlockArgParseResult parseArguments(@NotNull String[] args,
                                      @NotNull BlockData blockData);
}
