package com.willfp.eco.core.placeholder;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.placeholder.context.PlaceholderContext;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A placeholder that does not require a player, matched against a regex pattern.
 * <p>
 * The text matched by the pattern is passed to the function as the arguments.
 */
public final class DynamicPlaceholder implements RegistrablePlaceholder {
    /**
     * The placeholder pattern.
     */
    private final Pattern pattern;

    /**
     * The function to retrieve the value of the placeholder from its arguments.
     */
    private final Function<@NotNull String, @Nullable String> function;

    /**
     * The plugin that owns the placeholder.
     */
    private final EcoPlugin plugin;

    /**
     * The pattern in the form shown to users, or null to show the regex itself.
     */
    private final String displayPattern;

    /**
     * Create a new dynamic placeholder.
     *
     * @param plugin   The plugin.
     * @param pattern  The pattern.
     * @param function The function to retrieve the value.
     */
    public DynamicPlaceholder(@NotNull final EcoPlugin plugin,
                              @NotNull final Pattern pattern,
                              @NotNull final Function<@NotNull String, @Nullable String> function) {
        this(plugin, pattern, null, function);
    }

    /**
     * Create a new dynamic placeholder that advertises itself as something other than its regex.
     *
     * @param plugin         The plugin.
     * @param pattern        The pattern.
     * @param displayPattern The pattern as shown to users, e.g. {@code mining_top_<N>_name}.
     * @param function       The function to retrieve the value.
     */
    public DynamicPlaceholder(@NotNull final EcoPlugin plugin,
                              @NotNull final Pattern pattern,
                              @Nullable final String displayPattern,
                              @NotNull final Function<@NotNull String, @Nullable String> function) {
        this.plugin = plugin;
        this.pattern = pattern;
        this.displayPattern = displayPattern;
        this.function = function;
    }

    @Override
    @Nullable
    public String getValue(@NotNull final String args,
                           @NotNull final PlaceholderContext context) {
        return function.apply(args);
    }

    /**
     * Get the value of the placeholder.
     *
     * @param args The args.
     * @return The value, or an empty string if the function returned null.
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

    @NotNull
    @Override
    public String getDisplayPattern() {
        return Objects.requireNonNullElseGet(
                this.displayPattern,
                RegistrablePlaceholder.super::getDisplayPattern
        );
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
