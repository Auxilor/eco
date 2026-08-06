package com.willfp.eco.core.blocks.args;

import com.willfp.eco.core.blocks.TestableBlock;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An argument parser that should generate a test for a block, along with a
 * modifier to apply to blocks created by {@link TestableBlock#place(Location)}.
 */
public interface BlockArgParser {
    /**
     * Parse the arguments.
     *
     * @param args      The arguments.
     * @param blockData The block data of the base block, used to determine whether
     *                  the arguments apply.
     * @return The parse result, or null if none of the arguments applied to this parser.
     */
    @Nullable BlockArgParseResult parseArguments(@NotNull String[] args,
                                                 @NotNull BlockData blockData);
}
