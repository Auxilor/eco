package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.entities.ai.EntityGoal;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

/**
 * Be tempted by holding items.
 *
 * @param speed       The speed at which the entity follows the item.
 * @param items       The items that the entity will be attracted by.
 * @param canBeScared If the entity can be scared and lose track of the item.
 */
public record EntityGoalTempt(
        double speed,
        Collection<ItemStack> items,
        boolean canBeScared
) implements EntityGoal<Mob> {

}
