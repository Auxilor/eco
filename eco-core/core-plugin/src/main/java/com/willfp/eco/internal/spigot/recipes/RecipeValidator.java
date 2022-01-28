package com.willfp.eco.internal.spigot.recipes;

import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;

public interface RecipeValidator {

    boolean validate(PrepareItemCraftEvent event);

    boolean validate(CraftItemEvent event);

}
