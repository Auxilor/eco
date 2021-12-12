package com.willfp.eco.internal.spigot.recipes;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.PluginDependent;
import com.willfp.eco.core.items.Items;
import com.willfp.eco.core.items.TestableItem;
import com.willfp.eco.core.recipe.Recipes;
import com.willfp.eco.core.recipe.parts.MaterialTestableItem;
import com.willfp.eco.core.recipe.parts.ModifiedTestableItem;
import com.willfp.eco.core.recipe.parts.TestableStack;
import com.willfp.eco.core.recipe.recipes.CraftingRecipe;
import com.willfp.eco.core.recipe.recipes.ShapedCraftingRecipe;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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

public class ShapedRecipeListener extends PluginDependent<EcoPlugin> implements Listener {
    public ShapedRecipeListener(@NotNull final EcoPlugin plugin) {
        super(plugin);
    }

    private void allow(@NotNull final Event event,
                       @NotNull final CraftingRecipe recipe) {
        if (event instanceof PrepareItemCraftEvent) {
            ((PrepareItemCraftEvent) event).getInventory().setResult(recipe.getOutput());
        }

        if (event instanceof CraftItemEvent) {
            ((CraftItemEvent) event).getInventory().setResult(recipe.getOutput());
        }
    }

    private void deny(@NotNull final Event event) {
        if (event instanceof PrepareItemCraftEvent) {
            ((PrepareItemCraftEvent) event).getInventory().setResult(new ItemStack(Material.AIR));
        }

        if (event instanceof CraftItemEvent) {
            ((CraftItemEvent) event).getInventory().setResult(new ItemStack(Material.AIR));
            ((CraftItemEvent) event).setResult(Event.Result.DENY);
            ((CraftItemEvent) event).setCancelled(true);
        }
    }

    @EventHandler
    public void complexRecipeListener(@NotNull final PrepareItemCraftEvent event) {
        if (!(event.getRecipe() instanceof ShapedRecipe recipe)) {
            return;
        }

        if (!EcoPlugin.getPluginNames().contains(recipe.getKey().getNamespace())) {
            return;
        }

        if (!(event.getInventory().getViewers().get(0) instanceof Player player)) {
            return;
        }

        ItemStack[] matrix = event.getInventory().getMatrix();
        CraftingRecipe matched = Recipes.getMatch(matrix);

        if (matched == null) {
            deny(event);
            return;
        }

        if (matched.test(matrix)) {
            if (matched.getPermission() != null) {
                if (player.hasPermission(matched.getPermission())) {
                    allow(event, matched);
                } else {
                    deny(event);
                }
            } else {
                allow(event, matched);
            }
        } else {
            deny(event);
        }
    }

    @EventHandler
    public void complexRecipeListener(@NotNull final CraftItemEvent event) {
        if (!(event.getRecipe() instanceof ShapedRecipe recipe)) {
            return;
        }

        if (!EcoPlugin.getPluginNames().contains(recipe.getKey().getNamespace())) {
            return;
        }

        if (!(event.getInventory().getViewers().get(0) instanceof Player player)) {
            return;
        }

        ItemStack[] matrix = event.getInventory().getMatrix();
        CraftingRecipe matched = Recipes.getMatch(matrix);

        if (matched == null) {
            deny(event);
            return;
        }

        if (matched.test(matrix)) {
            if (matched.getPermission() != null) {
                if (player.hasPermission(matched.getPermission())) {
                    allow(event, matched);
                } else {
                    deny(event);
                }
            } else {
                allow(event, matched);
            }
        } else {
            deny(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void stackedRecipeListener(@NotNull final CraftItemEvent event) {
        if (!(event.getRecipe() instanceof ShapedRecipe recipe)) {
            return;
        }

        if (!EcoPlugin.getPluginNames().contains(recipe.getKey().getNamespace())) {
            return;
        }

        ItemStack[] matrix = event.getInventory().getMatrix();
        CraftingRecipe matched = Recipes.getMatch(matrix);

        if (matched == null) {
            return;
        }

        boolean isStackedRecipe = false;

        int upperBound = 64;
        for (int i = 0; i < 9; i++) {
            ItemStack inMatrix = event.getInventory().getMatrix()[i];
            TestableItem inRecipe = matched.getParts().get(i);

            if (inRecipe instanceof TestableStack testableStack) {
                int max = Math.floorDiv(inMatrix.getAmount(), testableStack.getAmount());
                if (max < upperBound) {
                    upperBound = max;
                }
                isStackedRecipe = true;
            } else if (inMatrix != null) {
                int max = inMatrix.getAmount();
                if (max < upperBound) {
                    upperBound = max;
                }
            }
        }

        if (!isStackedRecipe) {
            return;
        }

        int toGivePerRecipe = event.getRecipe().getResult().getAmount();
        int maxStackSize = event.getRecipe().getResult().getMaxStackSize();
        while (toGivePerRecipe * upperBound > maxStackSize) {
            upperBound--;
        }

        for (int i = 0; i < 9; i++) {
            ItemStack inMatrix = event.getInventory().getMatrix()[i];
            TestableItem inRecipe = matched.getParts().get(i);

            if (inRecipe instanceof TestableStack testableStack) {
                if (event.isShiftClick()) {
                    int amount = inMatrix.getAmount() + 1;
                    for (int j = 0; j < upperBound; j++) {
                        amount -= testableStack.getAmount();
                    }
                    inMatrix.setAmount(amount);
                } else {
                    inMatrix.setAmount(inMatrix.getAmount() - (testableStack.getAmount() - 1));
                }
            }
        }

        int finalUpperBound = upperBound;

        if (event.isShiftClick()) {
            ItemStack result = event.getInventory().getResult();
            if (result == null) {
                return;
            }

            result.setAmount(result.getAmount() * finalUpperBound);
            event.getInventory().setResult(result);
        }
    }

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
            if (part instanceof ModifiedTestableItem modified) {
                if (modified.getHandle() instanceof MaterialTestableItem) {
                    if (Items.isCustomItem(itemStack)) {
                        event.getInventory().setResult(new ItemStack(Material.AIR));
                        return;
                    }
                }
            }
            if (part instanceof TestableStack modified) {
                if (modified.getHandle() instanceof MaterialTestableItem) {
                    if (Items.isCustomItem(itemStack)) {
                        event.getInventory().setResult(new ItemStack(Material.AIR));
                        return;
                    }
                }
            }
        }
    }

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
            if (part instanceof ModifiedTestableItem modified) {
                if (modified.getHandle() instanceof MaterialTestableItem) {
                    if (Items.isCustomItem(itemStack)) {
                        event.getInventory().setResult(new ItemStack(Material.AIR));
                        event.setResult(Event.Result.DENY);
                        event.setCancelled(true);
                        return;
                    }
                }
            }
            if (part instanceof TestableStack modified) {
                if (modified.getHandle() instanceof MaterialTestableItem) {
                    if (Items.isCustomItem(itemStack)) {
                        event.getInventory().setResult(new ItemStack(Material.AIR));
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void preventUsingComplexPartInVanillaRecipe(@NotNull final PrepareItemCraftEvent event) {
        if (!(event.getRecipe() instanceof Keyed recipe)) {
            return;
        }

        if (EcoPlugin.getPluginNames().contains(recipe.getKey().getNamespace())) {
            return;
        }

        for (ItemStack itemStack : event.getInventory().getMatrix()) {
            if (Items.isCustomItem(itemStack)) {
                event.getInventory().setResult(new ItemStack(Material.AIR));
                return;
            }
        }
    }

    @EventHandler
    public void preventUsingComplexPartInVanillaRecipe(@NotNull final CraftItemEvent event) {
        if (!(event.getRecipe() instanceof Keyed recipe)) {
            return;
        }

        if (EcoPlugin.getPluginNames().contains(recipe.getKey().getNamespace())) {
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

    @EventHandler
    public void preventLearningDisplayedRecipes(@NotNull final PlayerRecipeDiscoverEvent event) {
        if (!EcoPlugin.getPluginNames().contains(event.getRecipe().getNamespace())) {
            return;
        }

        if (event.getRecipe().getKey().contains("_displayed")) {
            event.setCancelled(true);
        }
    }
}
