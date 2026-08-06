package com.willfp.eco.core.placeholder.context;

import com.willfp.eco.core.placeholder.AdditionalPlayer;
import com.willfp.eco.core.placeholder.InjectablePlaceholder;
import com.willfp.eco.core.placeholder.PlaceholderInjectable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a context to translate placeholders in.
 */
public class PlaceholderContext {
    /**
     * An empty injectable, used when no injectable context is provided.
     */
    private static final PlaceholderInjectable EMPTY_INJECTABLE = new PlaceholderInjectable() {
        @Override
        public void addInjectablePlaceholder(@NotNull final Iterable<InjectablePlaceholder> placeholders) {
            // Do nothing.
        }

        @Override
        public void clearInjectedPlaceholders() {
            // Do nothing.
        }

        @Override
        public @NotNull List<InjectablePlaceholder> getPlaceholderInjections() {
            return Collections.emptyList();
        }
    };

    /**
     * An empty context.
     */
    public static final PlaceholderContext EMPTY = new PlaceholderContext(
            null,
            null,
            null,
            Collections.emptyList()
    );

    /**
     * The player, or null if the context has no player.
     */
    @Nullable
    private final Player player;

    /**
     * The ItemStack, or null if the context has no item.
     */
    @Nullable
    private final ItemStack itemStack;

    /**
     * The {@link PlaceholderInjectable} context.
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
        this(null, null, null, Collections.emptyList());
    }

    /**
     * Create a PlaceholderContext for a player.
     *
     * @param player The player, or null for no player.
     */
    public PlaceholderContext(@Nullable final Player player) {
        this(player, null, null, Collections.emptyList());
    }

    /**
     * Create a new PlaceholderContext with the given parameters.
     *
     * @param player            The player, or null for no player.
     * @param itemStack         The ItemStack, or null for no item.
     * @param injectableContext The injectable context, or null for an empty injectable context.
     * @param additionalPlayers The additional players.
     */
    public PlaceholderContext(@Nullable final Player player,
                              @Nullable final ItemStack itemStack,
                              @Nullable final PlaceholderInjectable injectableContext,
                              @NotNull final Collection<AdditionalPlayer> additionalPlayers) {
        this.player = player;
        this.itemStack = itemStack;
        this.injectableContext = Objects.requireNonNullElse(injectableContext, EMPTY_INJECTABLE);
        this.additionalPlayers = additionalPlayers;
    }

    /**
     * Get the player.
     *
     * @return The player, or null if the context has no player.
     */
    @Nullable
    public Player getPlayer() {
        return player;
    }

    /**
     * Get the ItemStack.
     *
     * @return The ItemStack, or null if the context has no item.
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
     * Copy this context with a different player.
     *
     * @param player The player, or null for no player.
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
     * Copy this context with a different item.
     *
     * @param itemStack The ItemStack, or null for no item.
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
     * Copy this context with an extra injectable context, merged with the existing one.
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
     * Create a PlaceholderContext from a {@link PlaceholderInjectable} context.
     *
     * @param injectableContext The injectable context.
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
