package com.willfp.eco.core.requirement;

import com.willfp.eco.core.Eco;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Contains methods and fields pertaining to requirements.
 *
 * @deprecated See {@link Requirement}.
 */
@ApiStatus.ScheduledForRemoval(inVersion = "6.27.0")
@Deprecated(since = "6.24.0", forRemoval = true)
@SuppressWarnings("removal")
public final class Requirements {
    /**
     * Requires a player to have a permission.
     */
    public static final Requirement HAS_PERMISSION = new com.willfp.eco.core.requirement.impl.RequirementHasPermission();

    /**
     * Placeholder equals value.
     */
    public static final Requirement PLACEHOLDER_EQUALS = new com.willfp.eco.core.requirement.impl.RequirementPlaceholderEquals();

    /**
     * Numeric placeholder greater than value.
     */
    public static final Requirement PLACEHOLDER_GREATER_THAN = new com.willfp.eco.core.requirement.impl.RequirementPlaceholderGreaterThan();

    /**
     * Numeric placeholder less than value.
     */
    public static final Requirement PLACEHOLDER_LESS_THAN = new com.willfp.eco.core.requirement.impl.RequirementPlaceholderLessThan();

    /**
     * Get Requirements matching ID.
     *
     * @param name The ID to search for.
     * @return The matching Requirements.
     */
    public static Requirement getByID(@NotNull final String name) {
        return switch (name.toLowerCase()) {
            case "has-permission" -> HAS_PERMISSION;
            case "placeholder-equals" -> PLACEHOLDER_EQUALS;
            case "placeholder-greater-than" -> PLACEHOLDER_GREATER_THAN;
            case "placeholder-less-than" -> PLACEHOLDER_LESS_THAN;
            default -> new Requirement() {
                @Override
                public boolean doesPlayerMeet(@NotNull final Player player,
                                              @NotNull final List<String> args) {
                    return true;
                }
            };
        };
    }

    private Requirements() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    static {
        Eco.getHandler().getEcoPlugin().getLogger().severe("Loading for-removal Requirements system! This will throw an error once 6.27.0 is released."
                + "Make sure you're running the latest version of all your plugins!");
    }
}
