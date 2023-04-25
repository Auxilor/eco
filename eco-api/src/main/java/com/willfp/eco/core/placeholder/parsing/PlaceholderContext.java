package com.willfp.eco.core.placeholder.parsing;

import com.willfp.eco.core.integrations.placeholder.PlaceholderManager;
import com.willfp.eco.core.math.MathContext;
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
 */
public class PlaceholderContext extends MathContext {
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
     * The ItemStack.
     */
    @Nullable
    private final ItemStack itemStack;

    /**
     * Constructs a new PlaceholderContext with the given parameters.
     *
     * @param player            The player.
     * @param itemStack         The ItemStack.
     * @param injectableContext The PlaceholderInjectable parseContext.
     * @param additionalPlayers The additional players.
     */
    public PlaceholderContext(@Nullable final Player player,
                              @Nullable final ItemStack itemStack,
                              @NotNull final PlaceholderInjectable injectableContext,
                              @NotNull final Collection<AdditionalPlayer> additionalPlayers) {
        super(injectableContext, player, additionalPlayers);

        this.itemStack = itemStack;
    }

    /**
     * Get the ItemStack.
     *
     * @return The ItemStack.
     */
    @Nullable
    public ItemStack getItemStack() {
        return itemStack;
    }

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
                this.getItemStack(),
                this.getInjectableContext(),
                this.getAdditionalPlayers()
        );
    }
}
