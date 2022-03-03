package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.entities.ai.EntityGoal;
import com.willfp.eco.core.serialization.KeyedDeserializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Cat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Allows a cat to sit on a bed.
 *
 * @param speed The speed at which to move to the bed.
 */
public record EntityGoalCatSitOnBed(
        double speed
) implements EntityGoal<Cat> {
    /**
     * The deserializer for the goal.
     */
    public static final KeyedDeserializer<EntityGoalCatSitOnBed> DESERIALIZER = new EntityGoalCatSitOnBed.Deserializer();

    /**
     * Deserialize configs into the goal.
     */
    private static final class Deserializer implements KeyedDeserializer<EntityGoalCatSitOnBed> {
        @Override
        @Nullable
        public EntityGoalCatSitOnBed deserialize(@NotNull final Config config) {
            if (!(
                    config.has("speed")
            )) {
                return null;
            }

            try {
                return new EntityGoalCatSitOnBed(
                        config.getDouble("speed")
                );
            } catch (Exception e) {
                /*
                Exceptions could be caused by configs having values of a wrong type,
                invalid enum parameters, etc. Serializers shouldn't throw exceptions,
                so we encapsulate them as null.
                 */
                return null;
            }
        }

        @NotNull
        @Override
        public NamespacedKey getKey() {
            return NamespacedKey.minecraft("cat_sit_on_bed");
        }
    }
}
