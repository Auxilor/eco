package com.willfp.eco.core.requirement;

import com.willfp.eco.core.Eco;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Interface for internal requirement factory implementations.
 */
@ApiStatus.Internal
@Eco.HandlerComponent
@Deprecated(since = "6.24.0", forRemoval = true)
@SuppressWarnings({"removal", "DeprecatedIsStillUsed"})
public interface RequirementFactory {
    /**
     * Create a requirement.
     *
     * @param name The name.
     * @return The requirement returned for the name.
     * Will return a requirement that is always true if not found.
     */
    Requirement create(@NotNull String name);
}
