package com.willfp.eco.core.placeholder;

import com.willfp.eco.core.placeholder.context.PlaceholderContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiFunction;

/**
 * A single attribute configuration for a {@link KeyedPlaceholder}.
 * <p>
 * Each attribute declares a name (the suffix portion of the placeholder
 * identifier, e.g. "name", "description", "completed_description") and
 * a value function that receives the resolved key 'chain' and the
 * placeholder context, returning the attribute's string value or null.
 * <p>
 * The value function receives the player via {@code context.getPlayer()}.
 * Player-dependent attributes should return null if the player is null.
 * Playerless attributes can ignore the player.
 * <p>
 * Instances are immutable and safe to share across threads.
 */
public record KeyedPlaceholderAttribute(
    String name,
    BiFunction<@NotNull List<@NotNull Object>, @NotNull PlaceholderContext, @Nullable String> valueFunction
) {

    /**
     * Create a new attribute.
     *
     * @param name          The attribute name (suffix). May contain underscores.
     * @param valueFunction The value function. Receives the fully resolved
     *                      key 'chain' (unmodifiable, all non-null) and the
     *                      placeholder context. Returns the string value
     *                      or null.
     */
    public KeyedPlaceholderAttribute(
        @NotNull final String name,
        @NotNull final BiFunction<@NotNull List<@NotNull Object>, @NotNull PlaceholderContext, @Nullable String> valueFunction
    ) {
        this.name = name;
        this.valueFunction = valueFunction;
    }

    @Override
    @NotNull
    public String name() {
        return name;
    }

    @Override
    @NotNull
    public BiFunction<@NotNull List<@NotNull Object>, @NotNull PlaceholderContext, @Nullable String> valueFunction() {
        return valueFunction;
    }

}