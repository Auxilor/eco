package com.willfp.eco.core.entities.args;

import com.willfp.eco.core.entities.TestableEntity;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An argument parser should generate the predicate as well
 * as modify the Entity for {@link TestableEntity#spawn(Location)}.
 */
public interface EntityArgParser {
    /**
     * Parse the arguments.
     *
     * @param args The arguments.
     * @return The parse result, or null if the arguments are not applicable to this parser.
     */
    @Nullable EntityArgParseResult parseArguments(@NotNull String[] args);
}
