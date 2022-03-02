package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.entities.ai.EntityGoal;
import org.bukkit.entity.Mob;

/**
 * Attack like an ocelot.
 */
public record EntityGoalOcelotAttack(
) implements EntityGoal<Mob> {

}
