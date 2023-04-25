package com.willfp.eco.core.placeholder.parsing;

import com.willfp.eco.core.integrations.placeholder.PlaceholderManager;
import com.willfp.eco.core.placeholder.AdditionalPlayer;
import com.willfp.eco.core.placeholder.PlaceholderInjectable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

/**
 * Represents a context to translate placeholders in.
 *
 * @param player            The player.
 * @param itemStack         The ItemStack.
 * @param injectableContext The injectable context.
 * @param additionalPlayers The additional players.
 */
public record PlaceholderContext(
        @Nullable Player player,
        @Nullable ItemStack itemStack,
        @NotNull PlaceholderInjectable injectableContext,
        @NotNull Collection<AdditionalPlayer> additionalPlayers
) {
    /**
     * An empty context.
     */
    public static final PlaceholderContext EMPTY = new PlaceholderContext(
            null,
            null,
            PlaceholderManager.EMPTY_INJECTABLE,
            Collections.emptyList()
    );

    /**
     * Create MathContext of a PlaceholderInjectable parseContext.
     *
     * @param injectableContext The PlaceholderInjectable parseContext.
     * @return The MathContext.
     */
    public static PlaceholderContext of(@NotNull final PlaceholderInjectable injectableContext) {
        return new PlaceholderContext(
                null,
                null,
                injectableContext,
                Collections.emptyList()
        );
    }

    /**
     * Copy with a player.
     *
     * @param player The player.
     * @return The new MathContext.
     */
    public PlaceholderContext copyWithPlayer(@Nullable final Player player) {
        return new PlaceholderContext(
                player,
                this.itemStack(),
                this.injectableContext(),
                this.additionalPlayers()
        );
    }
}
