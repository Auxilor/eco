package com.willfp.eco.core.requirement;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A requirement is a defined goal that a player must meet.
 */
@Deprecated(since = "6.24.0", forRemoval = true)
@SuppressWarnings("DeprecatedIsStillUsed")
public abstract class Requirement {
    /**
     * Create a new requirement.
     */
    protected Requirement() {

    }

    /**
     * Test if the player meets the requirement.
     *
     * @param player The player.
     * @param args   The arguments.
     * @return The requirement.
     */
    public abstract boolean doesPlayerMeet(@NotNull Player player,
                                           @NotNull List<String> args);
}
