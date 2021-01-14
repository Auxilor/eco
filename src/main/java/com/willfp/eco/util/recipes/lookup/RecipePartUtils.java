package com.willfp.eco.util.recipes.lookup;

import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import com.willfp.eco.util.recipes.parts.ComplexRecipePart;
import com.willfp.eco.util.recipes.parts.EmptyRecipePart;
import com.willfp.eco.util.recipes.parts.RecipePart;
import com.willfp.eco.util.recipes.parts.SimpleRecipePart;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.scoreboard.ScoreboardManager;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
@UtilityClass
public final class RecipePartUtils {
    /**
     * Instance of registered item lookup utils.
     */
    private final Object instance;

    /**
     * The class of the utils.
     */
    private final Class<?> clazz;

    /**
     * The test service registered to bukkit.
     */
    private final Method lookupMethod;

    /**
     * The register service registered to bukkit.
     */
    private final Method registerMethod;

    /**
     * Lookup recipe part from string.
     *
     * @param key The string to test.
     * @return The generated recipe part, or null if invalid.
     */
    public RecipePart lookup(@NotNull final String key) {
        Object recipePartUncast;

        try {
            recipePartUncast = lookupMethod.invoke(instance, key);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return new EmptyRecipePart();
        }

        if (recipePartUncast.getClass().toString().contains("EmptyRecipePart")) {
            return new EmptyRecipePart();
        }

        try {
            if (recipePartUncast.getClass().toString().contains("SimpleRecipePart")) {
                Material material = (Material) recipePartUncast.getClass().getDeclaredMethod("getMaterial").invoke(recipePartUncast);
                return new SimpleRecipePart(material);
            }

            if (recipePartUncast.getClass().toString().contains("ComplexRecipePart")) {
                Predicate<ItemStack> predicate = (Predicate<ItemStack>) recipePartUncast.getClass().getDeclaredMethod("getPredicate").invoke(recipePartUncast);
                ItemStack displayed = (ItemStack) recipePartUncast.getClass().getDeclaredMethod("getDisplayed").invoke(recipePartUncast);

                return new ComplexRecipePart(predicate, displayed);
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return new EmptyRecipePart();
    }

    /**
     * Register a new lookup.
     *
     * @param key    The key of the lookup.
     * @param lookup The lookup to register, where the output is the recipe part generated.
     */
    public void registerLookup(@NotNull final String key,
                               @NotNull final Function<String, RecipePart> lookup) {
        try {
            registerMethod.invoke(instance, key, lookup);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    static {
        Method lookupMethod1;
        Method registerMethod1;
        if (Bukkit.getServicesManager().getKnownServices().stream().noneMatch(clazz -> clazz.getName().contains("ItemLookup"))) {
            Bukkit.getServicesManager().register(ItemLookup.class, new EcoItemLookup(), AbstractEcoPlugin.getInstance(), ServicePriority.Normal);
        }

        instance = Bukkit.getServicesManager().load(Bukkit.getServicesManager().getKnownServices().stream().filter(clazz -> clazz.getName().contains("ItemLookup")).findFirst().get());
        clazz = instance.getClass();

        try {
            lookupMethod1 = clazz.getDeclaredMethod("lookup", String.class);
            registerMethod1 = clazz.getDeclaredMethod("registerLookup", String.class, Function.class);
            lookupMethod1.setAccessible(true);
            registerMethod1.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            lookupMethod1 = null;
            registerMethod1 = null;
        }

        lookupMethod = lookupMethod1;
        registerMethod = registerMethod1;
    }
}
