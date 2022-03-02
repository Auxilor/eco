package com.willfp.eco.core.entities.ai.target;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.entities.Entities;
import com.willfp.eco.core.entities.TestableEntity;
import com.willfp.eco.core.entities.ai.TargetGoal;
import com.willfp.eco.core.serialization.KeyedDeserializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Raider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
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
    /**
     * The deserializer for the goal.
     */
    public static final KeyedDeserializer<TargetGoalNearestAttackableWitch> DESERIALIZER = new TargetGoalNearestAttackableWitch.Deserializer();

    /**
     * Deserialize configs into the goal.
     */
    @SuppressWarnings("unchecked")
    private static final class Deserializer implements KeyedDeserializer<TargetGoalNearestAttackableWitch> {
        @Override
        @Nullable
        public TargetGoalNearestAttackableWitch deserialize(@NotNull final Config config) {
            if (!(
                    config.has("targetClass")
                            && config.has("checkVisibility")
                            && config.has("checkCanNavigate")
                            && config.has("reciprocalChance")
                            && config.has("targetFilter")
            )) {
                return null;
            }

            try {
                TestableEntity filter = Entities.lookup(config.getString("targetFilter"));

                return new TargetGoalNearestAttackableWitch(
                        (Class<? extends LivingEntity>)
                                Objects.requireNonNull(
                                        EntityType.valueOf(config.getString("avoidClass").toUpperCase()).getEntityClass()
                                ),
                        config.getBool("checkVisibility"),
                        config.getBool("checkCanNavigate"),
                        config.getInt("reciprocalChance"),
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
            return NamespacedKey.minecraft("nearest_attackable_witch");
        }
    }
}
