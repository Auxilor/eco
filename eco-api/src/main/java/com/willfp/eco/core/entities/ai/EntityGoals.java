package com.willfp.eco.core.entities.ai;

import com.google.common.collect.HashBiMap;
import com.willfp.eco.core.entities.ai.entity.EntityGoalAvoidEntity;
import com.willfp.eco.core.serialization.KeyedDeserializer;
import org.bukkit.NamespacedKey;
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

    /**
     * minecraft:avoid_entity.
     */
    public static final KeyedDeserializer<EntityGoalAvoidEntity> AVOID_ENTITY = register(EntityGoalAvoidEntity.DESERIALIZER);

    /**
     * Get deserializer by key.
     *
     * @param key The key.
     * @return The deserializer, or null if not found.
     */
    @Nullable
    public static KeyedDeserializer<? extends EntityGoal<?>> getByKey(@NotNull final NamespacedKey key) {
        return BY_KEY.get(key);
    }

    private static <T extends KeyedDeserializer<? extends EntityGoal<?>>> T register(@NotNull final T toRegister) {
        BY_KEY.put(toRegister.getKey(), toRegister);
        return toRegister;
    }

    private EntityGoals() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
