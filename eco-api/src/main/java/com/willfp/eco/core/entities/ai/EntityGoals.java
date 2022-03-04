package com.willfp.eco.core.entities.ai;

import com.google.common.collect.HashBiMap;
import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.entities.ai.entity.EntityGoalAvoidEntity;
import com.willfp.eco.core.entities.ai.entity.EntityGoalBreakDoors;
import com.willfp.eco.core.entities.ai.entity.EntityGoalBreatheAir;
import com.willfp.eco.core.entities.ai.entity.EntityGoalBreed;
import com.willfp.eco.core.entities.ai.entity.EntityGoalCatLieOnBed;
import com.willfp.eco.core.entities.ai.entity.EntityGoalCatSitOnBed;
import com.willfp.eco.core.entities.ai.entity.EntityGoalEatGrass;
import com.willfp.eco.core.entities.ai.entity.EntityGoalFleeSun;
import com.willfp.eco.core.entities.ai.entity.EntityGoalFloat;
import com.willfp.eco.core.entities.ai.entity.EntityGoalFollowBoats;
import com.willfp.eco.core.entities.ai.entity.EntityGoalFollowMobs;
import com.willfp.eco.core.entities.ai.entity.EntityGoalIllusionerBlindnessSpell;
import com.willfp.eco.core.entities.ai.entity.EntityGoalIllusionerMirrorSpell;
import com.willfp.eco.core.entities.ai.entity.EntityGoalInteract;
import com.willfp.eco.core.entities.ai.entity.EntityGoalLeapAtTarget;
import com.willfp.eco.core.entities.ai.entity.EntityGoalLookAtPlayer;
import com.willfp.eco.core.entities.ai.entity.EntityGoalMeleeAttack;
import com.willfp.eco.core.entities.ai.entity.EntityGoalMoveBackToVillage;
import com.willfp.eco.core.entities.ai.entity.EntityGoalMoveThroughVillage;
import com.willfp.eco.core.entities.ai.entity.EntityGoalMoveTowardsRestriction;
import com.willfp.eco.core.entities.ai.entity.EntityGoalMoveTowardsTarget;
import com.willfp.eco.core.entities.ai.entity.EntityGoalOcelotAttack;
import com.willfp.eco.core.entities.ai.entity.EntityGoalOpenDoors;
import com.willfp.eco.core.entities.ai.entity.EntityGoalPanic;
import com.willfp.eco.core.entities.ai.entity.EntityGoalRandomLookAround;
import com.willfp.eco.core.entities.ai.entity.EntityGoalRandomStroll;
import com.willfp.eco.core.entities.ai.entity.EntityGoalRandomSwimming;
import com.willfp.eco.core.entities.ai.entity.EntityGoalRangedAttack;
import com.willfp.eco.core.entities.ai.entity.EntityGoalRangedBowAttack;
import com.willfp.eco.core.entities.ai.entity.EntityGoalRangedCrossbowAttack;
import com.willfp.eco.core.entities.ai.entity.EntityGoalRestrictSun;
import com.willfp.eco.core.entities.ai.entity.EntityGoalStrollThroughVillage;
import com.willfp.eco.core.entities.ai.entity.EntityGoalTempt;
import com.willfp.eco.core.entities.ai.entity.EntityGoalTryFindWater;
import com.willfp.eco.core.entities.ai.entity.EntityGoalUseItem;
import com.willfp.eco.core.entities.ai.entity.EntityGoalWaterAvoidingRandomFlying;
import com.willfp.eco.core.entities.ai.entity.EntityGoalWaterAvoidingRandomStroll;
import com.willfp.eco.core.entities.ai.entity.EntityGoalWolfBeg;
import com.willfp.eco.core.serialization.KeyedDeserializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Class to manage entity goals.
 */
public final class EntityGoals {
    /**
     * All registered deserializers.
     */
    private static final Map<NamespacedKey, KeyedDeserializer<? extends EntityGoal<?>>> BY_KEY = HashBiMap.create();

    static {
        register(EntityGoalAvoidEntity.DESERIALIZER);
        register(EntityGoalBreakDoors.DESERIALIZER);
        register(EntityGoalBreatheAir.DESERIALIZER);
        register(EntityGoalEatGrass.DESERIALIZER);
        register(EntityGoalFleeSun.DESERIALIZER);
        register(EntityGoalFloat.DESERIALIZER);
        register(EntityGoalFollowBoats.DESERIALIZER);
        register(EntityGoalFollowMobs.DESERIALIZER);
        register(EntityGoalInteract.DESERIALIZER);
        register(EntityGoalLeapAtTarget.DESERIALIZER);
        register(EntityGoalLookAtPlayer.DESERIALIZER);
        register(EntityGoalMeleeAttack.DESERIALIZER);
        register(EntityGoalMoveBackToVillage.DESERIALIZER);
        register(EntityGoalMoveThroughVillage.DESERIALIZER);
        register(EntityGoalMoveTowardsRestriction.DESERIALIZER);
        register(EntityGoalMoveTowardsTarget.DESERIALIZER);
        register(EntityGoalOcelotAttack.DESERIALIZER);
        register(EntityGoalOpenDoors.DESERIALIZER);
        register(EntityGoalPanic.DESERIALIZER);
        register(EntityGoalRandomLookAround.DESERIALIZER);
        register(EntityGoalRandomStroll.DESERIALIZER);
        register(EntityGoalRandomSwimming.DESERIALIZER);
        register(EntityGoalRangedAttack.DESERIALIZER);
        register(EntityGoalRangedBowAttack.DESERIALIZER);
        register(EntityGoalRangedCrossbowAttack.DESERIALIZER);
        register(EntityGoalRestrictSun.DESERIALIZER);
        register(EntityGoalStrollThroughVillage.DESERIALIZER);
        register(EntityGoalTempt.DESERIALIZER);
        register(EntityGoalTryFindWater.DESERIALIZER);
        register(EntityGoalUseItem.DESERIALIZER);
        register(EntityGoalWaterAvoidingRandomFlying.DESERIALIZER);
        register(EntityGoalWaterAvoidingRandomStroll.DESERIALIZER);
        register(EntityGoalWolfBeg.DESERIALIZER);
        register(EntityGoalBreed.DESERIALIZER);
        register(EntityGoalCatSitOnBed.DESERIALIZER);
        register(EntityGoalCatLieOnBed.DESERIALIZER);
        register(EntityGoalIllusionerBlindnessSpell.DESERIALIZER);
        register(EntityGoalIllusionerMirrorSpell.DESERIALIZER);
    }

    /**
     * Get deserializer by key.
     *
     * @param key The key.
     * @return The deserializer, or null if not found.
     */
    @Nullable
    public static KeyedDeserializer<? extends EntityGoal<? extends Mob>> getByKey(@NotNull final NamespacedKey key) {
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
    public static <T extends Mob> KeyedDeserializer<EntityGoal<T>> getByKeyOfType(@NotNull final NamespacedKey key,
                                                                                  @NotNull final Class<T> clazz) {
        return (KeyedDeserializer<EntityGoal<T>>) BY_KEY.get(key);
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
        KeyedDeserializer<EntityGoal<T>> deserializer = getByKeyOfType(key, (Class<T>) entity.getClass());
        if (deserializer == null) {
            return entity;
        }

        EntityGoal<T> goal = deserializer.deserialize(config);

        if (goal == null) {
            return entity;
        }

        return goal.addToEntity(entity, priority);
    }

    /**
     * Register a deserializer for an entity goal.
     *
     * @param toRegister The entity goal to register.
     * @param <T>        The type of deserializer.
     * @return The deserializer.
     */
    @NotNull
    public static <T extends KeyedDeserializer<? extends EntityGoal<?>>> T register(@NotNull final T toRegister) {
        BY_KEY.put(toRegister.getKey(), toRegister);
        return toRegister;
    }

    private EntityGoals() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
