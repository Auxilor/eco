package com.willfp.eco.util;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * Utilities / API methods for telekinesis.
 * <p>
 * Telekinesis means that all drops and xp go straight to a player's inventory
 * rather than dropping on the ground.
 */
public final class TelekinesisUtils {
    /**
     * Set of tests that return if the player is telekinetic.
     */
    private static final Set<Function<Player, Boolean>> TESTS = new HashSet<>();

    /**
     * Register a new test to check against.
     *
     * @param test The test to register, where the boolean output is if the player is telekinetic.
     */
    public static void registerTest(@NotNull final Function<Player, Boolean> test) {
        TESTS.add(test);
    }

    /**
     * Test the player for telekinesis.
     * <p>
     * If any test returns true, so does this.
     *
     * @param player The player to test.
     * @return If the player is telekinetic.
     */
    public static boolean testPlayer(@NotNull final Player player) {
        for (Function<Player, Boolean> test : TESTS) {
            if (test.apply(player)) {
                return true;
            }
        }

        return false;
    }

    private TelekinesisUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
