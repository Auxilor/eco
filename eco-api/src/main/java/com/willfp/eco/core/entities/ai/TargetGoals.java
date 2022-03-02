package com.willfp.eco.core.entities.ai;

import com.google.common.collect.HashBiMap;
import com.willfp.eco.core.entities.ai.target.TargetGoalDefendVillage;
import com.willfp.eco.core.entities.ai.target.TargetGoalHurtBy;
import com.willfp.eco.core.entities.ai.target.TargetGoalNearestAttackable;
import com.willfp.eco.core.entities.ai.target.TargetGoalNearestAttackableWitch;
import com.willfp.eco.core.entities.ai.target.TargetGoalNearestHealableRaider;
import com.willfp.eco.core.entities.ai.target.TargetGoalNonTameRandom;
import com.willfp.eco.core.entities.ai.target.TargetGoalOwnerHurt;
import com.willfp.eco.core.entities.ai.target.TargetGoalOwnerHurtBy;
import com.willfp.eco.core.entities.ai.target.TargetGoalResetUniversalAnger;
import com.willfp.eco.core.serialization.KeyedDeserializer;
import org.bukkit.NamespacedKey;
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
        register(TargetGoalOwnerHurt.DESERIALIZER);
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
    public static KeyedDeserializer<? extends TargetGoal<?>> getByKey(@NotNull final NamespacedKey key) {
        return BY_KEY.get(key);
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
