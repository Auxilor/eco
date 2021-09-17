package com.willfp.eco.core.requirement;

import com.willfp.eco.core.Eco;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

/**
 * Contains methods and fields pertaining to requirements.
 */
@UtilityClass
public class Requirements {
    /**
     * Requires a player to have a permission.
     */
    public static final Requirement HAS_PERMISSION = Eco.getHandler().getRequirementFactory().create("has-permission");

    /**
     * Placeholder equals value.
     */
    public static final Requirement PLACEHOLDER_EQUALS = Eco.getHandler().getRequirementFactory().create("placeholder-equals");

    /**
     * Numeric placeholder greater than value.
     */
    public static final Requirement PLACEHOLDER_GREATER_THAN = Eco.getHandler().getRequirementFactory().create("placeholder-greater-than");

    /**
     * Numeric placeholder less than value.
     */
    public static final Requirement PLACEHOLDER_LESS_THAN = Eco.getHandler().getRequirementFactory().create("placeholder-less-than");

    /**
     * Get Requirements matching ID.
     *
     * @param name The ID to search for.
     * @return The matching Requirements.
     */
    public static Requirement getByID(@NotNull final String name) {
        return Eco.getHandler().getRequirementFactory().create(name);
    }
}
