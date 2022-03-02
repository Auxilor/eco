package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.entities.ai.EntityGoal;
import org.bukkit.entity.Mob;

import java.util.function.BooleanSupplier;

/**
 * Move through village.
 *
 * @param speed                     The speed at which to move through the village.
 * @param onlyAtNight               If the entity can only move through village at night.
 * @param distance                  The distance to move through the village.
 * @param canPassThroughDoorsGetter A getter for if the entity can pass through doors.
 */
public record EntityGoalMoveThroughVillage(
        double speed,
        boolean onlyAtNight,
        int distance,
        BooleanSupplier canPassThroughDoorsGetter
) implements EntityGoal<Mob> {

}
