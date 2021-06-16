package com.willfp.eco.spigot.recipes;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.PluginDependent;
import com.willfp.eco.core.items.Items;
import com.willfp.eco.core.items.TestableItem;
import com.willfp.eco.core.recipe.Recipes;
import com.willfp.eco.core.recipe.parts.MaterialTestableItem;
import com.willfp.eco.core.recipe.parts.TestableStack;
import com.willfp.eco.core.recipe.recipes.CraftingRecipe;
import com.willfp.eco.core.recipe.recipes.ShapedCraftingRecipe;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerRecipeDiscoverEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

public class ShapedRecipeListener extends PluginDependent implements Listener {
    /**
     * Pass an {@link EcoPlugin} in order to interface with it.
     *
     * @param plugin The plugin to manage.
     */
    public ShapedRecipeListener(@NotNull final EcoPlugin plugin) {
        super(plugin);
    }

    /**
     * Called on item craft.
     *
     * @param event The event to listen for.
     */
    @EventHandler
    public void complexRecipeListener(@NotNull final PrepareItemCraftEvent event) {
        if (!(event.getRecipe() instanceof ShapedRecipe recipe)) {
            return;
        }

        if (!EcoPlugin.LOADED_ECO_PLUGINS.contains(recipe.getKey().getNamespace())) {
            return;
        }

        ItemStack[] matrix = event.getInventory().getMatrix();
        CraftingRecipe matched = Recipes.getMatch(matrix);

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
        if (!(event.getRecipe() instanceof ShapedRecipe recipe)) {
            return;
        }

        if (!EcoPlugin.LOADED_ECO_PLUGINS.contains(recipe.getKey().getNamespace())) {
            return;
        }

        ItemStack[] matrix = event.getInventory().getMatrix();
        CraftingRecipe matched = Recipes.getMatch(matrix);

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
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void stackedRecipeListener(@NotNull final CraftItemEvent event) {
        if (!(event.getRecipe() instanceof ShapedRecipe recipe)) {
            return;
        }

        if (!EcoPlugin.LOADED_ECO_PLUGINS.contains(recipe.getKey().getNamespace())) {
            return;
        }

        ItemStack[] matrix = event.getInventory().getMatrix();
        CraftingRecipe matched = Recipes.getMatch(matrix);

        if (matched == null) {
            return;
        }

        this.getPlugin().getScheduler().runLater(() -> {
            for (int i = 0; i < 9; i++) {
                ItemStack inMatrix = event.getInventory().getMatrix()[i];
                TestableItem inRecipe = matched.getParts().get(i);

                if (inRecipe instanceof TestableStack testableStack) {
                    inMatrix.setAmount(inMatrix.getAmount() - (testableStack.getAmount() - 1));
                }
            }
        }, 1);
    }

    /**
     * Called on item craft.
     *
     * @param event The event to listen for.
     */
    @EventHandler
    public void preventUsingComplexPartInEcoRecipe(@NotNull final PrepareItemCraftEvent event) {
        if (!(event.getRecipe() instanceof ShapedRecipe recipe)) {
            return;
        }

        CraftingRecipe craftingRecipe = Recipes.getRecipe(recipe.getKey());

        if (!(craftingRecipe instanceof ShapedCraftingRecipe shapedCraftingRecipe)) {
            return;
        }

        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = event.getInventory().getMatrix()[i];
            TestableItem part = shapedCraftingRecipe.getParts().get(i);
            if (part instanceof MaterialTestableItem) {
                if (Items.isCustomItem(itemStack)) {
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
        if (!(event.getRecipe() instanceof ShapedRecipe recipe)) {
            return;
        }

        CraftingRecipe craftingRecipe = Recipes.getRecipe(recipe.getKey());

        if (!(craftingRecipe instanceof ShapedCraftingRecipe shapedCraftingRecipe)) {
            return;
        }

        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = event.getInventory().getMatrix()[i];
            TestableItem part = shapedCraftingRecipe.getParts().get(i);
            if (part instanceof MaterialTestableItem) {
                if (Items.isCustomItem(itemStack)) {
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
        if (!(event.getRecipe() instanceof ShapedRecipe recipe)) {
            return;
        }

        if (EcoPlugin.LOADED_ECO_PLUGINS.contains(recipe.getKey().getNamespace())) {
            return;
        }

        for (ItemStack itemStack : event.getInventory().getMatrix()) {
            if (Items.isCustomItem(itemStack)) {
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
        if (!(event.getRecipe() instanceof ShapedRecipe recipe)) {
            return;
        }

        if (EcoPlugin.LOADED_ECO_PLUGINS.contains(recipe.getKey().getNamespace())) {
            return;
        }

        for (ItemStack itemStack : event.getInventory().getMatrix()) {
            if (Items.isCustomItem(itemStack)) {
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
        if (!EcoPlugin.LOADED_ECO_PLUGINS.contains(event.getRecipe().getNamespace())) {
            return;
        }

        if (event.getRecipe().getKey().contains("_displayed")) {
            event.setCancelled(true);
        }
    }
}
