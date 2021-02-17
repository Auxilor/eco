package com.willfp.eco.spigot.recipes;

import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import com.willfp.eco.util.recipe.RecipeParts;
import com.willfp.eco.util.recipe.Recipes;
import com.willfp.eco.util.recipe.parts.RecipePart;
import com.willfp.eco.util.recipe.parts.SimpleRecipePart;
import com.willfp.eco.util.recipe.recipes.EcoShapedRecipe;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerRecipeDiscoverEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

public class RecipeListener implements Listener {
    /**
     * Called on item craft.
     *
     * @param event The event to listen for.
     */
    @EventHandler
    public void complexRecipeListener(@NotNull final PrepareItemCraftEvent event) {
        if (!(event.getRecipe() instanceof ShapedRecipe)) {
            return;
        }

        ShapedRecipe recipe = (ShapedRecipe) event.getRecipe();

        if (!AbstractEcoPlugin.LOADED_ECO_PLUGINS.contains(recipe.getKey().getNamespace())) {
            return;
        }

        ItemStack[] matrix = event.getInventory().getMatrix();
        EcoShapedRecipe matched = Recipes.getMatch(matrix);

        if (matched == null) {
            event.getInventory().setResult(new ItemStack(Material.AIR));
            return;
        }

        if (matched.test(matrix)) {
            event.getInventory().setResult(matched.getOutput());
        } else {
            event.getInventory().setResult(new ItemStack(Material.AIR));
        }
    }

    /**
     * Called on item craft.
     *
     * @param event The event to listen for.
     */
    @EventHandler
    public void complexRecipeListener(@NotNull final CraftItemEvent event) {
        if (!(event.getRecipe() instanceof ShapedRecipe)) {
            return;
        }

        ShapedRecipe recipe = (ShapedRecipe) event.getRecipe();

        if (!AbstractEcoPlugin.LOADED_ECO_PLUGINS.contains(recipe.getKey().getNamespace())) {
            return;
        }

        ItemStack[] matrix = event.getInventory().getMatrix();
        EcoShapedRecipe matched = Recipes.getMatch(matrix);

        if (matched == null) {
            event.getInventory().setResult(new ItemStack(Material.AIR));
            event.setResult(Event.Result.DENY);
            event.setCancelled(true);
            return;
        }

        if (matched.test(matrix)) {
            event.getInventory().setResult(matched.getOutput());
        } else {
            event.getInventory().setResult(new ItemStack(Material.AIR));
            event.setResult(Event.Result.DENY);
            event.setCancelled(true);
        }
    }

    /**
     * Called on item craft.
     *
     * @param event The event to listen for.
     */
    @EventHandler
    public void preventUsingComplexPartInEcoRecipe(@NotNull final PrepareItemCraftEvent event) {
        if (!(event.getRecipe() instanceof ShapedRecipe)) {
            return;
        }

        ShapedRecipe recipe = (ShapedRecipe) event.getRecipe();

        EcoShapedRecipe ecoShapedRecipe = Recipes.getShapedRecipe(recipe.getKey());

        if (ecoShapedRecipe == null) {
            return;
        }

        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = event.getInventory().getMatrix()[i];
            RecipePart part = ecoShapedRecipe.getParts()[i];
            if (part instanceof SimpleRecipePart) {
                if (RecipeParts.isRecipePart(itemStack)) {
                    event.getInventory().setResult(new ItemStack(Material.AIR));
                    return;
                }
            }
        }
    }

    /**
     * Called on item craft.
     *
     * @param event The event to listen for.
     */
    @EventHandler
    public void preventUsingComplexPartInEcoRecipe(@NotNull final CraftItemEvent event) {
        if (!(event.getRecipe() instanceof ShapedRecipe)) {
            return;
        }

        ShapedRecipe recipe = (ShapedRecipe) event.getRecipe();

        EcoShapedRecipe ecoShapedRecipe = Recipes.getShapedRecipe(recipe.getKey());

        if (ecoShapedRecipe == null) {
            return;
        }

        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = event.getInventory().getMatrix()[i];
            RecipePart part = ecoShapedRecipe.getParts()[i];
            if (part instanceof SimpleRecipePart) {
                if (RecipeParts.isRecipePart(itemStack)) {
                    event.getInventory().setResult(new ItemStack(Material.AIR));
                    event.setResult(Event.Result.DENY);
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    /**
     * Prevents using talismans in recipes.
     *
     * @param event The event to listen for.
     */
    @EventHandler
    public void preventUsingComplexPartInVanillaRecipe(@NotNull final PrepareItemCraftEvent event) {
        if (!(event.getRecipe() instanceof ShapedRecipe)) {
            return;
        }

        ShapedRecipe recipe = (ShapedRecipe) event.getRecipe();

        if (AbstractEcoPlugin.LOADED_ECO_PLUGINS.contains(recipe.getKey().getNamespace())) {
            return;
        }

        for (ItemStack itemStack : event.getInventory().getMatrix()) {
            if (RecipeParts.isRecipePart(itemStack)) {
                event.getInventory().setResult(new ItemStack(Material.AIR));
                return;
            }
        }
    }

    /**
     * Prevents using talismans in recipes.
     *
     * @param event The event to listen for.
     */
    @EventHandler
    public void preventUsingComplexPartInVanillaRecipe(@NotNull final CraftItemEvent event) {
        if (!(event.getRecipe() instanceof ShapedRecipe)) {
            return;
        }

        ShapedRecipe recipe = (ShapedRecipe) event.getRecipe();

        if (AbstractEcoPlugin.LOADED_ECO_PLUGINS.contains(recipe.getKey().getNamespace())) {
            return;
        }

        for (ItemStack itemStack : event.getInventory().getMatrix()) {
            if (RecipeParts.isRecipePart(itemStack)) {
                event.getInventory().setResult(new ItemStack(Material.AIR));
                event.setResult(Event.Result.DENY);
                event.setCancelled(true);
                return;
            }
        }
    }

    /**
     * Prevents learning displayed recipes.
     *
     * @param event The event to listen for.
     */
    @EventHandler
    public void preventLearningDisplayedRecipes(@NotNull final PlayerRecipeDiscoverEvent event) {
        if (!AbstractEcoPlugin.LOADED_ECO_PLUGINS.contains(event.getRecipe().getNamespace())) {
            return;
        }

        if (event.getRecipe().getKey().contains("_displayed")) {
            event.setCancelled(true);
        }
    }
}
