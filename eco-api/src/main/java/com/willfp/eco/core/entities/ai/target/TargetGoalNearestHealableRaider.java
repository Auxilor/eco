package com.willfp.eco.core.entities.ai.target;

import com.willfp.eco.core.entities.ai.TargetGoal;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Raider;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * Target nearest attackable raider.
 *
 * @param targetClass     The types of entities to heal.
 * @param checkVisibility If visibility should be checked.
 * @param targetFilter    The filter for targets to match.
 */
public record TargetGoalNearestHealableRaider(
        @NotNull Class<? extends LivingEntity> targetClass,
        boolean checkVisibility,
        @NotNull Predicate<LivingEntity> targetFilter
) implements TargetGoal<Raider> {

}
