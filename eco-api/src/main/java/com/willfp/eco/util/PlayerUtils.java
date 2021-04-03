package com.willfp.eco.util;

import com.willfp.eco.util.optional.Prerequisite;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

@UtilityClass
public class PlayerUtils {
    /**
     * If the meta set function has been set.
     */
    private boolean initialized = false;

    /**
     * The cooldown function.
     */
    private Function<Player, Double> cooldownFunction = null;

    /**
     * Get the attack cooldown for a player.
     *
     * @param player The player's attack cooldown.
     * @return A value between 0 and 1, with 1 representing full power.
     */
    public double getAttackCooldown(@NotNull final Player player) {
        Validate.isTrue(initialized, "Must be initialized!");
        Validate.notNull(cooldownFunction, "Must be initialized!");

        if (Prerequisite.MINIMUM_1_16.isMet()) {
            return player.getAttackCooldown();
        }

        return cooldownFunction.apply(player);
    }

    /**
     * Initialize the cooldown function.
     *
     * @param function The function.
     */
    @ApiStatus.Internal
    public void initialize(@NotNull final Function<Player, Double> function) {
        Validate.isTrue(!initialized, "Already initialized!");

        cooldownFunction = function;

        initialized = true;
    }
}
