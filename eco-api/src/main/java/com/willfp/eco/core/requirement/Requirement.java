package com.willfp.eco.core.requirement;

import com.willfp.eco.core.Eco;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A requirement is a defined goal that a player must meet.
 *
 * @deprecated No typing, weak definitions, and not an API component. Shouldn't be in eco.
 */
@ApiStatus.ScheduledForRemoval(inVersion = "6.27.0")
@Deprecated(since = "6.24.0", forRemoval = true)
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

    static {
        Eco.getHandler().getEcoPlugin().getLogger().severe("Loading for-removal Requirements system! This will throw an error once 6.27.0 is released."
                + "Make sure you're running the latest version of all your plugins!");
    }
}
