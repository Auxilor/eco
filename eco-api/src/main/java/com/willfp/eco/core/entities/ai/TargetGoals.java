package com.willfp.eco.core.entities.ai;

import com.google.common.collect.HashBiMap;
import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.entities.ai.target.TargetGoalDefendVillage;
import com.willfp.eco.core.entities.ai.target.TargetGoalHurtBy;
import com.willfp.eco.core.entities.ai.target.TargetGoalNearestAttackable;
import com.willfp.eco.core.entities.ai.target.TargetGoalNearestAttackableWitch;
import com.willfp.eco.core.entities.ai.target.TargetGoalNearestHealableRaider;
import com.willfp.eco.core.entities.ai.target.TargetGoalNonTameRandom;
import com.willfp.eco.core.entities.ai.target.TargetGoalOwnerHurtBy;
import com.willfp.eco.core.entities.ai.target.TargetGoalOwnerTarget;
import com.willfp.eco.core.entities.ai.target.TargetGoalResetUniversalAnger;
import com.willfp.eco.core.serialization.KeyedDeserializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Class to manage target goals.
 */
public final class TargetGoals {
    /**
     * All registered deserializers.
     */
    private static final Map<NamespacedKey, KeyedDeserializer<? extends TargetGoal<?>>> BY_KEY = HashBiMap.create();

    static {
        register(TargetGoalDefendVillage.DESERIALIZER);
        register(TargetGoalHurtBy.DESERIALIZER);
        register(TargetGoalNearestAttackable.DESERIALIZER);
        register(TargetGoalNearestAttackableWitch.DESERIALIZER);
        register(TargetGoalNearestHealableRaider.DESERIALIZER);
        register(TargetGoalNonTameRandom.DESERIALIZER);
        register(TargetGoalOwnerTarget.DESERIALIZER);
        register(TargetGoalOwnerHurtBy.DESERIALIZER);
        register(TargetGoalResetUniversalAnger.DESERIALIZER);
    }

    /**
     * Get deserializer by key.
     *
     * @param key The key.
     * @return The deserializer, or null if not found.
     */
    @Nullable
    public static KeyedDeserializer<? extends TargetGoal<? extends Mob>> getByKey(@NotNull final NamespacedKey key) {
        return BY_KEY.get(key);
    }

    /**
     * Get deserializer by key, with a defined type (to prevent cluttering code with unsafe casts).
     *
     * @param key   The key.
     * @param clazz The type of target goal.
     * @param <T>   The type of mob the goal can be applied to.
     * @return The deserializer, or null if not found.
     */
    @Nullable
    @SuppressWarnings({"unchecked", "unused"})
    public static <T extends Mob> KeyedDeserializer<TargetGoal<T>> getByKeyOfType(@NotNull final NamespacedKey key,
                                                                                  @NotNull final Class<T> clazz) {
        return (KeyedDeserializer<TargetGoal<T>>) BY_KEY.get(key);
    }

    /**
     * Apply goal to entity given key and config.
     * <p>
     * If the key or config are invalid, the goal will not be applied.
     *
     * @param entity   The entity.
     * @param key      The key.
     * @param config   The config.
     * @param priority The priority.
     * @param <T>      The entity type.
     * @return The entity.
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public static <T extends Mob> T applyToEntity(@NotNull final T entity,
                                                  @NotNull final NamespacedKey key,
                                                  @NotNull final Config config,
                                                  final int priority) {
        KeyedDeserializer<TargetGoal<T>> deserializer = getByKeyOfType(key, (Class<T>) entity.getClass());
        if (deserializer == null) {
            return entity;
        }

        TargetGoal<T> goal = deserializer.deserialize(config);

        if (goal == null) {
            return entity;
        }

        return goal.addToEntity(entity, priority);
    }

    /**
     * Register a deserializer for a target goal.
     *
     * @param toRegister The target goal to register.
     * @param <T>        The type of deserializer.
     * @return The deserializer.
     */
    @NotNull
    public static <T extends KeyedDeserializer<? extends TargetGoal<?>>> T register(@NotNull final T toRegister) {
        BY_KEY.put(toRegister.getKey(), toRegister);
        return toRegister;
    }

    private TargetGoals() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
