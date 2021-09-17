package com.willfp.eco.core.requirement;

import org.jetbrains.annotations.NotNull;

/**
 * Interface for internal requirement factory implementations.
 */
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
