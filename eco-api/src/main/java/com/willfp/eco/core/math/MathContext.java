package com.willfp.eco.core.math;

import com.willfp.eco.core.integrations.placeholder.PlaceholderManager;
import com.willfp.eco.core.placeholder.AdditionalPlayer;
import com.willfp.eco.core.placeholder.PlaceholderInjectable;
import com.willfp.eco.core.placeholder.context.PlaceholderContext;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * Represents a context to parse math in.
 *
 * @deprecated Use {@link PlaceholderContext} instead.
 */
@SuppressWarnings("DeprecatedIsStillUsed")
@Deprecated(since = "6.56.0", forRemoval = true)
public class MathContext {
    /**
     * Returns an empty math parseContext.
     */
    public static final MathContext EMPTY = new MathContext(
            PlaceholderManager.EMPTY_INJECTABLE,
            null,
            Collections.emptyList()
    );

    /**
     * The PlaceholderInjectable parse context.
     */
    @NotNull
    private final PlaceholderInjectable injectableContext;

    /**
     * The player.
     */
    @Nullable
    private final Player player;

    /**
     * The additional players.
     */
    @NotNull
    private final Collection<AdditionalPlayer> additionalPlayers;

    /**
     * Constructs a new MathContext with the given parameters.
     *
     * @param injectableContext The PlaceholderInjectable parseContext.
     * @param player            The player.
     * @param additionalPlayers The additional players.
     */
    public MathContext(@NotNull PlaceholderInjectable injectableContext,
                       @Nullable Player player,
                       @NotNull Collection<AdditionalPlayer> additionalPlayers) {
        this.injectableContext = injectableContext;
        this.player = player;
        this.additionalPlayers = additionalPlayers;
    }

    /**
     * Returns the PlaceholderInjectable parse context.
     * <p>
     * Duplicate method because MathContext used to be a record.
     *
     * @return The injectable context.
     * @deprecated Use {@link #getInjectableContext()} instead.
     */
    @Deprecated(since = "6.56.0", forRemoval = true)
    public PlaceholderInjectable injectableContext() {
        return injectableContext;
    }

    /**
     * Returns the PlaceholderInjectable parse context.
     *
     * @return The injectable context.
     */
    @NotNull
    public PlaceholderInjectable getInjectableContext() {
        return injectableContext;
    }

    /**
     * Returns the player.
     * <p>
     * Duplicate method because MathContext used to be a record.
     *
     * @return The player.
     * @deprecated Use {@link #getPlayer()} instead.
     */
    @Deprecated(since = "6.56.0", forRemoval = true)
    @Nullable
    public Player player() {
        return player;
    }

    /**
     * Returns the player.
     *
     * @return The player.
     */
    @Nullable
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns the additional players.
     * <p>
     * Duplicate method because MathContext used to be a record.
     *
     * @return The additional players.
     * @deprecated Use {@link #getAdditionalPlayers()} instead.
     */
    @Deprecated(since = "6.56.0", forRemoval = true)
    @NotNull
    public Collection<AdditionalPlayer> additionalPlayers() {
        return additionalPlayers;
    }

    /**
     * Returns the additional players.
     *
     * @return The additional players.
     */
    @NotNull
    public Collection<AdditionalPlayer> getAdditionalPlayers() {
        return additionalPlayers;
    }

    /**
     * Convert to PlaceholderContext.
     *
     * @return The PlaceholderContext.
     */
    @NotNull
    public PlaceholderContext toPlaceholderContext() {
        return new PlaceholderContext(
                this.player,
                null,
                this.injectableContext,
                this.additionalPlayers
        );
    }

    @Override
    public String toString() {
        return "MathContext{" +
                "injectableContext=" + injectableContext +
                ", player=" + player +
                ", additionalPlayers=" + additionalPlayers +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof MathContext that)) {
            return false;
        }

        return injectableContext.equals(that.injectableContext) &&
                Objects.equals(player, that.player) &&
                additionalPlayers.equals(that.additionalPlayers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(injectableContext, player, additionalPlayers);
    }

    /**
     * Create MathContext of a PlaceholderInjectable parseContext.
     *
     * @param injectableContext The PlaceholderInjectable parseContext.
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
     * @param context The parseContext.
     * @param player  The player.
     * @return The new MathContext.
     */
    public static MathContext copyWithPlayer(@NotNull final MathContext context,
                                             @Nullable final Player player) {
        return new MathContext(
                context.injectableContext,
                player,
                context.additionalPlayers
        );
    }
}
