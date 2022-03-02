package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.entities.Entities;
import com.willfp.eco.core.entities.TestableEntity;
import com.willfp.eco.core.entities.ai.EntityGoal;
import com.willfp.eco.core.serialization.KeyedDeserializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Avoid entities.
 *
 * @param avoidClass   The entity classes to avoid.
 * @param fleeDistance The distance to flee to.
 * @param slowSpeed    The slow movement speed.
 * @param fastSpeed    The fast movement speed.
 * @param filter       The filter for entities to check if they should be avoided.
 */
public record EntityGoalAvoidEntity(
        @NotNull Class<? extends LivingEntity> avoidClass,
        double fleeDistance,
        double slowSpeed,
        double fastSpeed,
        @NotNull Predicate<LivingEntity> filter
) implements EntityGoal<Mob> {
    /**
     * The deserializer for the goal.
     */
    public static final KeyedDeserializer<EntityGoalAvoidEntity> DESERIALIZER = new Deserializer();

    @SuppressWarnings("unchecked")
    private static final class Deserializer implements KeyedDeserializer<EntityGoalAvoidEntity> {
        @Override
        @Nullable
        public EntityGoalAvoidEntity deserialize(@NotNull final Config config) {
            if (!(
                    config.has("avoidClass")
                            && config.has("fleeDistance")
                            && config.has("slowSpeed")
                            && config.has("fastSpeed")
                            && config.has("filter")
            )) {
                return null;
            }

            try {
                TestableEntity filter = Entities.lookup(config.getString("filter"));

                return new EntityGoalAvoidEntity(
                        (Class<? extends LivingEntity>)
                                Objects.requireNonNull(
                                        EntityType.valueOf(config.getString("avoidClass").toUpperCase()).getEntityClass()
                                ),
                        config.getDouble("fleeDistance"),
                        config.getDouble("slowSpeed"),
                        config.getDouble("fastSpeed"),
                        filter::matches
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
            return NamespacedKey.minecraft("avoid_entity");
        }
    }
}
