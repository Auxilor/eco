package com.willfp.eco.core.entities.ai.entity;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.entities.ai.EntityGoal;
import com.willfp.eco.core.items.Items;
import com.willfp.eco.core.items.TestableItem;
import com.willfp.eco.core.recipe.parts.EmptyTestableItem;
import com.willfp.eco.core.serialization.KeyedDeserializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Allows an entity to be tempted by a set item.
 *
 * @param speed       The speed at which the entity follows the item.
 * @param items       The items that the entity will be attracted by.
 * @param canBeScared If the entity can be scared and lose track of the item.
 */
public record EntityGoalTempt(
        double speed,
        @NotNull Collection<TestableItem> items,
        boolean canBeScared
) implements EntityGoal<Mob> {
    /**
     * @param speed       The speed at which the entity follows the item.
     * @param item        The item that the entity will be attracted by.
     * @param canBeScared If the entity can be scared and lose track of the item.
     */
    public EntityGoalTempt(final double speed,
                           @NotNull final TestableItem item,
                           final boolean canBeScared) {
        this(speed, List.of(item), canBeScared);
    }

    /**
     * The deserializer for the goal.
     */
    public static final KeyedDeserializer<EntityGoalTempt> DESERIALIZER = new EntityGoalTempt.Deserializer();

    /**
     * Deserialize configs into the goal.
     */
    private static final class Deserializer implements KeyedDeserializer<EntityGoalTempt> {
        @Override
        @Nullable
        public EntityGoalTempt deserialize(@NotNull final Config config) {
            if (!(
                    config.has("speed")
                            && config.has("items")
                            && config.has("canBeScared")
            )) {
                return null;
            }

            Collection<TestableItem> items = config.getStrings("items").stream()
                    .map(Items::lookup)
                    .filter(it -> !(it instanceof EmptyTestableItem))
                    .collect(Collectors.toList());

            return new EntityGoalTempt(
                    config.getDouble("speed"),
                    items,
                    config.getBool("canBeScared")
            );
        }

        @NotNull
        @Override
        public NamespacedKey getKey() {
            return NamespacedKey.minecraft("tempt");
        }
    }
}
