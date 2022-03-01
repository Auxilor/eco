package com.willfp.eco.core.entities.ai.goals.entity;

import com.willfp.eco.core.entities.ai.goals.EntityGoal;

/**
 * Break doors.
 *
 * @param maxProgress The time taken to break the door (any integer above 240).
 */
public record EntityGoalBreakDoor(
        int maxProgress
) implements EntityGoal {

}
