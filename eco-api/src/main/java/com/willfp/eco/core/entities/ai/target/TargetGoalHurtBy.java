package com.willfp.eco.core.entities.ai.target;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.entities.ai.TargetGoal;
import com.willfp.eco.core.serialization.KeyedDeserializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Allows an entity to react when hit by set target.
 *
 * @param blacklist The entities not to attack when hurt by.
 */
@SuppressWarnings({"varargs", "unchecked"})
public record TargetGoalHurtBy(
        @NotNull Class<? extends LivingEntity>... blacklist
) implements TargetGoal<Mob> {
    /**
     * The deserializer for the goal.
     */
    public static final KeyedDeserializer<TargetGoalHurtBy> DESERIALIZER = new TargetGoalHurtBy.Deserializer();

    /**
     * Deserialize configs into the goal.
     */
    @SuppressWarnings("unchecked")
    private static final class Deserializer implements KeyedDeserializer<TargetGoalHurtBy> {
        @Override
        @Nullable
        public TargetGoalHurtBy deserialize(@NotNull final Config config) {
            if (!(
                    config.has("blacklist")
            )) {
                return null;
            }

            try {
                return new TargetGoalHurtBy(
                        (Class<? extends LivingEntity>[])
                                config.getStrings("blacklist").stream()
                                        .map(String::toUpperCase)
                                        .map(EntityType::valueOf)
                                        .map(EntityType::getEntityClass)
                                        .toArray()
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
            return NamespacedKey.minecraft("hurt_by");
        }
    }
}
