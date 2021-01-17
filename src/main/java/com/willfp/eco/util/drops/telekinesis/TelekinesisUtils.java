package com.willfp.eco.util.drops.telekinesis;

import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;

@UtilityClass
public final class TelekinesisUtils {
    /**
     * Instance of registered telekinesis tests.
     */
    private final TelekinesisTests tests;

    /**
     * Test the player for telekinesis.
     * <p>
     * If any test returns true, so does this.
     *
     * @param player The player to test.
     * @return If the player is telekinetic.
     */
    public boolean testPlayer(@NotNull final Player player) {
        return tests.testPlayer(player);
    }

    /**
     * Register a new test to check against.
     *
     * @param test The test to register, where the boolean output is if the player is telekinetic.
     */
    public void registerTest(@NotNull final Function<Player, Boolean> test) {
        tests.registerTest(test);
    }

    static {
        tests = Bukkit.getServicesManager().load(TelekinesisTests.class);
    }
}
