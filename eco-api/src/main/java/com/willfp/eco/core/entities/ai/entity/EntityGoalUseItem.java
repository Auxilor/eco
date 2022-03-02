package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.entities.Entities;
import com.willfp.eco.core.entities.TestableEntity;
import com.willfp.eco.core.entities.ai.EntityGoal;
import com.willfp.eco.core.items.Items;
import com.willfp.eco.core.serialization.KeyedDeserializer;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * Use item.
 *
 * @param item      The item.
 * @param sound     The sound to play on use.
 * @param condition The condition when to use the item.
 */
public record EntityGoalUseItem(
        @NotNull ItemStack item,
        @NotNull Sound sound,
        @NotNull Predicate<LivingEntity> condition
) implements EntityGoal<Mob> {
    /**
     * The deserializer for the goal.
     */
    public static final KeyedDeserializer<EntityGoalUseItem> DESERIALIZER = new EntityGoalUseItem.Deserializer();

    /**
     * Deserialize configs into the goal.
     */
    private static final class Deserializer implements KeyedDeserializer<EntityGoalUseItem> {
        @Override
        @Nullable
        public EntityGoalUseItem deserialize(@NotNull final Config config) {
            if (!(
                    config.has("item")
                            && config.has("sound")
                            && config.has("condition")
            )) {
                return null;
            }

            try {
                ItemStack item = Items.lookup(config.getString("item")).getItem();
                Sound sound = Sound.valueOf(config.getString("sound").toUpperCase());
                TestableEntity filter = Entities.lookup(config.getString("condition"));

                return new EntityGoalUseItem(
                        item,
                        sound,
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
            return NamespacedKey.minecraft("use_item");
        }
    }
}
