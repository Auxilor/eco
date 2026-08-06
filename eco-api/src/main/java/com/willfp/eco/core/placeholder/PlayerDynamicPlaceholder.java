package com.willfp.eco.core.placeholder;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.placeholder.context.PlaceholderContext;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.regex.Pattern;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A placeholder that requires a player, matched against a regex pattern.
 * <p>
 * The text matched by the pattern is passed to the function as the arguments. If the context has no player, the
 * placeholder resolves to null.
 */
public final class PlayerDynamicPlaceholder implements RegistrablePlaceholder {
    /**
     * The placeholder pattern.
     */
    private final Pattern pattern;

    /**
     * The function to retrieve the value of the placeholder from its arguments and a player.
     */
    private final BiFunction<@NotNull String, @NotNull Player, @Nullable String> function;

    /**
     * The plugin that owns the placeholder.
     */
    private final EcoPlugin plugin;

    /**
     * Create a new player dynamic placeholder.
     *
     * @param plugin   The plugin.
     * @param pattern  The pattern.
     * @param function The function to retrieve the value.
     */
    public PlayerDynamicPlaceholder(@NotNull final EcoPlugin plugin,
                                    @NotNull final Pattern pattern,
                                    @NotNull final BiFunction<@NotNull String, @NotNull Player, @Nullable String> function) {
        this.plugin = plugin;
        this.pattern = pattern;
        this.function = function;
    }

    @Override
    public @Nullable String getValue(@NotNull final String args,
                                     @NotNull final PlaceholderContext context) {
        Player player = context.getPlayer();

        if (player == null) {
            return null;
        }

        return function.apply(args, player);
    }

    /**
     * Get the value of the placeholder for a given player.
     *
     * @param args   The args.
     * @param player The player.
     * @return The value, or an empty string if the function returned null.
     * @deprecated Use {@link #getValue(String, PlaceholderContext)} instead.
     */
    @Deprecated(since = "6.56.0", forRemoval = true)
    @NotNull
    public String getValue(@NotNull final String args,
                           @NotNull final Player player) {
        return Objects.requireNonNullElse(
                function.apply(args, player),
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
        return this.pattern;
    }

    @Override
    public @NotNull PlayerDynamicPlaceholder register() {
        return (PlayerDynamicPlaceholder) RegistrablePlaceholder.super.register();
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof PlayerDynamicPlaceholder that)) {
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
