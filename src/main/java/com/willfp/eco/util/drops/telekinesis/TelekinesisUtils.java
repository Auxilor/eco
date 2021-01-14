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
     * Instance of registered telekinesis utils.
     */
    private final Object instance;

    /**
     * The class of the utils.
     */
    private final Class<?> clazz;

    /**
     * The test service registered to bukkit.
     */
    private final Method testMethod;

    /**
     * The register service registered to bukkit.
     */
    private final Method registerMethod;

    /**
     * Test the player for telekinesis.
     * <p>
     * If any test returns true, so does this.
     *
     * @param player The player to test.
     * @return If the player is telekinetic.
     */
    public boolean testPlayer(@NotNull final Player player) {
        try {
            return (boolean) testMethod.invoke(instance, player);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Register a new test to check against.
     *
     * @param test The test to register, where the boolean output is if the player is telekinetic.
     */
    public void registerTest(@NotNull final Function<Player, Boolean> test) {
        try {
            registerMethod.invoke(instance, test);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    static {
        Method testMethod1;
        Method registerMethod1;
        if (Bukkit.getServicesManager().getKnownServices().stream().noneMatch(clazz -> clazz.getName().contains("TelekinesisTests"))) {
            Bukkit.getServicesManager().register(TelekinesisTests.class, new EcoTelekinesisTests(), AbstractEcoPlugin.getInstance(), ServicePriority.Normal);
        }

        instance = Bukkit.getServicesManager().load(Bukkit.getServicesManager().getKnownServices().stream().filter(clazz -> clazz.getName().contains("TelekinesisTests")).findFirst().get());
        clazz = instance.getClass();

        try {
            testMethod1 = clazz.getDeclaredMethod("testPlayer", Player.class);
            registerMethod1 = clazz.getDeclaredMethod("registerTest", Function.class);
            testMethod1.setAccessible(true);
            registerMethod1.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            testMethod1 = null;
            registerMethod1 = null;
        }

        testMethod = testMethod1;
        registerMethod = registerMethod1;
    }
}
