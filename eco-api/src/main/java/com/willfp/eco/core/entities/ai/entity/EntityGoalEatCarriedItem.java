package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.entities.ai.EntityGoal;
import com.willfp.eco.core.serialization.KeyedDeserializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;

/**
 * Allows an entity to eat any item in its inventory and gain the benefits of the item.
 */
public record EntityGoalEatCarriedItem(
) implements EntityGoal<Mob> {
    /**
     * The deserializer for the goal.
     */
    public static final KeyedDeserializer<EntityGoalEatCarriedItem> DESERIALIZER = new EntityGoalEatCarriedItem.Deserializer();

    /**
     * Deserialize configs into the goal.
     */
    private static final class Deserializer implements KeyedDeserializer<EntityGoalEatCarriedItem> {
        @Override
        public EntityGoalEatCarriedItem deserialize(@NotNull final Config config) {
            return new EntityGoalEatCarriedItem();
        }

        @NotNull
        @Override
        public NamespacedKey getKey() {
            return NamespacedKey.minecraft("eat_carried_item");
        }
    }
}
