package com.willfp.eco.core.placeholder;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.placeholder.context.PlaceholderContext;
import com.willfp.eco.util.PatternUtils;

import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A placeholder that requires a player.
 * <p>
 * If the context has no player, the placeholder resolves to null.
 */
public final class PlayerPlaceholder implements RegistrablePlaceholder {
    /**
     * The raw identifier, used to lazily compile the pattern.
     */
    private final String rawIdentifier;

    /**
     * The placeholder pattern, lazily initialized from the raw identifier.
     */
    @Nullable
    private volatile Pattern pattern = null;

    /**
     * The function to retrieve the value of the placeholder for a player.
     */
    private final Function<@NotNull Player, @Nullable String> function;

    /**
     * The plugin that owns the placeholder.
     */
    private final EcoPlugin plugin;

    /**
     * Create a new player placeholder.
     *
     * @param plugin     The plugin.
     * @param identifier The identifier.
     * @param function   The function to retrieve the value.
     */
    public PlayerPlaceholder(@NotNull final EcoPlugin plugin,
                             @NotNull final String identifier,
                             @NotNull final Function<@NotNull Player, @Nullable String> function) {
        this.plugin = plugin;
        this.rawIdentifier = identifier;
        this.function = function;
    }

    @Override
    public @Nullable String getValue(@NotNull final String args,
                                     @NotNull final PlaceholderContext context) {
        Player player = context.getPlayer();

        if (player == null) {
            return null;
        }

        return function.apply(player);
    }

    /**
     * Get the value of the placeholder for a given player.
     *
     * @param player The player.
     * @return The value, or an empty string if the function returned null.
     * @deprecated Use {@link #getValue(String, PlaceholderContext)} instead.
     */
    @Deprecated(since = "6.56.0", forRemoval = true)
    @NotNull
    public String getValue(@NotNull final Player player) {
        return Objects.requireNonNullElse(
                function.apply(player),
                ""
        );
    }

    @Override
    public @NotNull EcoPlugin getPlugin() {
        return this.plugin;
    }

    @NotNull
    @Override
    public Pattern getPattern() {
        Pattern result = this.pattern;

        if (result == null) {
            synchronized (this) {
                result = this.pattern;
                if (result == null) {
                    result = PatternUtils.compileLiteral(this.rawIdentifier);
                    this.pattern = result;
                }
            }
        }

        return result;
    }

    @Override
    public @NotNull PlayerPlaceholder register() {
        return (PlayerPlaceholder) RegistrablePlaceholder.super.register();
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PlayerPlaceholder that)) {
            return false;
        }
        return Objects.equals(this.getPattern(), that.getPattern())
                && Objects.equals(this.getPlugin(), that.getPlugin());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getPattern(), this.getPlugin());
    }
}
