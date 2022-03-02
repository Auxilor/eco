package com.willfp.eco.core.entities.ai.target;

import com.willfp.eco.core.entities.ai.TargetGoal;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Raider;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * Allows an entity to attack the closest target within a given subset of specific target types.
 *
 * @param targetClass      The types of entities to attack.
 * @param checkVisibility  If visibility should be checked.
 * @param checkCanNavigate If navigation should be checked.
 * @param reciprocalChance 1 in reciprocalChance chance of not activating on any tick.
 * @param targetFilter     The filter for targets to match.
 */
public record TargetGoalNearestAttackableWitch(
        @NotNull Class<? extends LivingEntity> targetClass,
        boolean checkVisibility,
        boolean checkCanNavigate,
        int reciprocalChance,
        @NotNull Predicate<LivingEntity> targetFilter
) implements TargetGoal<Raider> {

}
