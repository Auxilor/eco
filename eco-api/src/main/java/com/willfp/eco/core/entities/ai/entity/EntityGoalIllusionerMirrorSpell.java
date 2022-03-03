package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.entities.ai.EntityGoal;
import com.willfp.eco.core.serialization.KeyedDeserializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Illusioner;
import org.jetbrains.annotations.NotNull;

/**
 * Allows an illusioner to perform the mirror spell.
 */
public record EntityGoalIllusionerMirrorSpell(
) implements EntityGoal<Illusioner> {
    /**
     * The deserializer for the goal.
     */
    public static final KeyedDeserializer<EntityGoalIllusionerMirrorSpell> DESERIALIZER = new EntityGoalIllusionerMirrorSpell.Deserializer();

    /**
     * Deserialize configs into the goal.
     */
    private static final class Deserializer implements KeyedDeserializer<EntityGoalIllusionerMirrorSpell> {
        @Override
        public EntityGoalIllusionerMirrorSpell deserialize(@NotNull final Config config) {
            return new EntityGoalIllusionerMirrorSpell();
        }

        @NotNull
        @Override
        public NamespacedKey getKey() {
            return NamespacedKey.minecraft("illusioner_mirror_spell");
        }
    }
}
