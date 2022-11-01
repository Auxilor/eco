package com.willfp.eco.core.math;

import com.willfp.eco.core.integrations.placeholder.PlaceholderManager;
import com.willfp.eco.core.placeholder.AdditionalPlayer;
import com.willfp.eco.core.placeholder.PlaceholderInjectable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

/**
 * Represents a context to do math in.
 *
 * @param injectableContext The PlaceholderInjectable context.
 * @param player            The player.
 * @param additionalPlayers The additional players.
 */
public record MathContext(
        @NotNull PlaceholderInjectable injectableContext,
        @Nullable Player player,
        @NotNull Collection<AdditionalPlayer> additionalPlayers
) {
    /**
     * Empty math context.
     */
    public static final MathContext EMPTY = new MathContext(
            PlaceholderManager.EMPTY_INJECTABLE,
            null,
            Collections.emptyList()
    );

    /**
     * Create MathContext of a PlaceholderInjectable context.
     *
     * @param injectableContext The PlaceholderInjectable context.
     * @return The MathContext.
     */
    public static MathContext of(@NotNull final PlaceholderInjectable injectableContext) {
        return new MathContext(
                injectableContext,
                null,
                Collections.emptyList()
        );
    }

    /**
     * Copy a MathContext with a player.
     *
     * @param context The context.
     * @param player  The player.
     * @return The new MathContext.
     */
    public static MathContext copyWithPlayer(@NotNull final MathContext context,
                                             @Nullable final Player player) {
        return new MathContext(
                context.injectableContext(),
                player,
                context.additionalPlayers()
        );
    }
}
