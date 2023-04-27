package com.willfp.eco.core.placeholder;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.placeholder.context.PlaceholderContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * A placeholder that does not require a player and supports dynamic styles.
 */
public final class DynamicPlaceholder implements RegistrablePlaceholder {
    /**
     * The arguments pattern.
     */
    private final Pattern pattern;

    /**
     * The function to retrieve the output of the arguments.
     */
    private final Function<@NotNull String, @Nullable String> function;

    /**
     * The plugin for the arguments.
     */
    private final EcoPlugin plugin;

    /**
     * Create a new dynamic arguments.
     *
     * @param plugin   The plugin.
     * @param pattern  The pattern.
     * @param function The function to retrieve the value.
     */
    public DynamicPlaceholder(@NotNull final EcoPlugin plugin,
                              @NotNull final Pattern pattern,
                              @NotNull final Function<@NotNull String, @Nullable String> function) {
        this.plugin = plugin;
        this.pattern = pattern;
        this.function = function;
    }

    @Override
    @Nullable
    public String getValue(@NotNull final String args,
                           @NotNull final PlaceholderContext context) {
        return function.apply(args);
    }

    /**
     * Get the value of the arguments.
     *
     * @param args The args.
     * @return The value.
     * @deprecated Use {@link #getValue(String, PlaceholderContext)} instead.
     */
    @Deprecated(since = "6.56.0", forRemoval = true)
    @NotNull
    public String getValue(@NotNull final String args) {
        return Objects.requireNonNullElse(
                function.apply(args),
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
    public @NotNull DynamicPlaceholder register() {
        return (DynamicPlaceholder) RegistrablePlaceholder.super.register();
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof DynamicPlaceholder that)) {
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
