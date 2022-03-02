package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.entities.ai.EntityGoal;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * Use item.
 *
 * @param item      The item.
 * @param sound     The sound to play on use.
 * @param condition The condition when to use the item.
 */
public record EntityGoalUseItem(
        @NotNull ItemStack item,
        @NotNull Sound sound,
        @NotNull Predicate<LivingEntity> condition
) implements EntityGoal<Mob> {

}
