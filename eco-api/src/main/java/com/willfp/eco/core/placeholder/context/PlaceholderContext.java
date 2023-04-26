package com.willfp.eco.core.placeholder.context;

import com.willfp.eco.core.integrations.placeholder.PlaceholderManager;
import com.willfp.eco.core.placeholder.AdditionalPlayer;
import com.willfp.eco.core.placeholder.PlaceholderInjectable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * Represents a context to translate placeholders in.
 */
public class PlaceholderContext {
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
     * The player.
     */
    @Nullable
    private final Player player;

    /**
     * The ItemStack.
     */
    @Nullable
    private final ItemStack itemStack;

    /**
     * The PlaceholderInjectable context.
     */
    @NotNull
    private final PlaceholderInjectable injectableContext;

    /**
     * The additional players.
     */
    @NotNull
    private final Collection<AdditionalPlayer> additionalPlayers;

    /**
     * Create an empty PlaceholderContext.
     */
    public PlaceholderContext() {
        this(null, null, PlaceholderManager.EMPTY_INJECTABLE, Collections.emptyList());
    }

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
        this.player = player;
        this.itemStack = itemStack;
        this.injectableContext = injectableContext;
        this.additionalPlayers = additionalPlayers;
    }

    /**
     * Get the player.
     *
     * @return The player.
     */
    @Nullable
    public Player getPlayer() {
        return player;
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
     * Get the PlaceholderInjectable context.
     *
     * @return The PlaceholderInjectable context.
     */
    @NotNull
    public PlaceholderInjectable getInjectableContext() {
        return injectableContext;
    }

    /**
     * Get the additional players.
     *
     * @return The additional players.
     */
    @NotNull
    public Collection<AdditionalPlayer> getAdditionalPlayers() {
        return additionalPlayers;
    }

    /**
     * Convert to a {@link com.willfp.eco.core.math.MathContext}.
     *
     * @return The math context.
     * @deprecated MathContext is deprecated, use {@link PlaceholderContext} instead.
     */
    @Deprecated(since = "6.56.0", forRemoval = true)
    @SuppressWarnings({"removal", "DeprecatedIsStillUsed"})
    public com.willfp.eco.core.math.MathContext toMathContext() {
        return new com.willfp.eco.core.math.MathContext(this.getInjectableContext(), this.getPlayer(), this.getAdditionalPlayers());
    }

    /**
     * Copy with a player.
     *
     * @param player The player.
     * @return The new context.
     */
    public PlaceholderContext copyWithPlayer(@Nullable final Player player) {
        return new PlaceholderContext(
                player,
                this.getItemStack(),
                this.getInjectableContext(),
                this.getAdditionalPlayers()
        );
    }

    /**
     * Copy with an item.
     *
     * @param itemStack The ItemStack.
     * @return The new context.
     */
    public PlaceholderContext copyWithItem(@Nullable final ItemStack itemStack) {
        return new PlaceholderContext(
                this.getPlayer(),
                itemStack,
                this.getInjectableContext(),
                this.getAdditionalPlayers()
        );
    }

    /**
     * Copy with an extra injectable context.
     *
     * @param injectableContext The injectable context to add.
     * @return The new context.
     */
    public PlaceholderContext withInjectableContext(@NotNull final PlaceholderInjectable injectableContext) {
        return new PlaceholderContext(
                this.getPlayer(),
                this.getItemStack(),
                new MergedInjectableContext(this.getInjectableContext(), injectableContext),
                this.getAdditionalPlayers()
        );
    }

    @Override
    public String toString() {
        return "PlaceholderContext{" +
                "player=" + player +
                ", itemStack=" + itemStack +
                ", injectableContext=" + injectableContext +
                ", additionalPlayers=" + additionalPlayers +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof PlaceholderContext that)) {
            return false;
        }

        return Objects.equals(
                getPlayer(), that.getPlayer())
                && Objects.equals(getItemStack(), that.getItemStack())
                && getInjectableContext().equals(that.getInjectableContext())
                && getAdditionalPlayers().equals(that.getAdditionalPlayers()
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPlayer(), getItemStack(), getInjectableContext(), getAdditionalPlayers());
    }

    /**
     * Create PlaceholderContext of a PlaceholderInjectable parseContext.
     *
     * @param injectableContext The PlaceholderInjectable parseContext.
     * @return The context.
     */
    public static PlaceholderContext of(@NotNull final PlaceholderInjectable injectableContext) {
        return new PlaceholderContext(
                null,
                null,
                injectableContext,
                Collections.emptyList()
        );
    }
}
