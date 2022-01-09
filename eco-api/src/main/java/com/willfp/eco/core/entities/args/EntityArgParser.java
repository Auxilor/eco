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
     * @return The predicate test to apply to the modified entity.
     */
    @Nullable EntityArgParseResult parseArguments(@NotNull String[] args);
}
