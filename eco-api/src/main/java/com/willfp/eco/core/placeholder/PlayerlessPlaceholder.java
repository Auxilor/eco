package com.willfp.eco.core.placeholder;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.placeholder.context.PlaceholderContext;
import com.willfp.eco.util.PatternUtils;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A arguments that does not require a player.
 */
public final class PlayerlessPlaceholder implements RegistrablePlaceholder {
    /**
     * The raw identifier.
     */
    private final String rawIdentifier;

    /**
     * The arguments pattern.
     */
    @Nullable
    private volatile Pattern pattern = null;

    /**
     * The function to retrieve the output of the arguments.
     */
    private final Supplier<@Nullable String> function;

    /**
     * The plugin for the arguments.
     */
    private final EcoPlugin plugin;

    /**
     * Create a new player arguments.
     *
     * @param plugin     The plugin.
     * @param identifier The identifier.
     * @param function   The function to retrieve the value.
     */
    public PlayerlessPlaceholder(@NotNull final EcoPlugin plugin,
                                 @NotNull final String identifier,
                                 @NotNull final Supplier<@Nullable String> function) {
        this.plugin = plugin;
        this.rawIdentifier = identifier;
        this.function = function;
    }

    @Override
    public @Nullable String getValue(@NotNull final String args,
                                     @NotNull final PlaceholderContext context) {
        return function.get();
    }

    /**
     * Get the value of the arguments.
     *
     * @return The value.
     * @deprecated Use {@link #getValue(String, PlaceholderContext)} instead.
     */
    @Deprecated(since = "6.56.0", forRemoval = true)
    @NotNull
    public String getValue() {
        return Objects.requireNonNullElse(
                this.function.get(),
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
    public @NotNull PlayerlessPlaceholder register() {
        return (PlayerlessPlaceholder) RegistrablePlaceholder.super.register();
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PlayerlessPlaceholder that)) {
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
