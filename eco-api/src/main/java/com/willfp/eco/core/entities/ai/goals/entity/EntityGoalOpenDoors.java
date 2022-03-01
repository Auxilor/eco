package com.willfp.eco.core.entities.ai.goals.entity;

import com.willfp.eco.core.entities.ai.goals.EntityGoal;

/**
 * Open doors.
 *
 * @param delayedClose If closing the door should be delayed.
 */
public record EntityGoalOpenDoors(
        boolean delayedClose
) implements EntityGoal {

}
