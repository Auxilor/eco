package com.willfp.eco.core.placeholder;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * A placeholder that does not require a player.
 */
public final class PlayerlessPlaceholder implements Placeholder {
    /**
     * The placeholder identifier.
     */
    private final String identifier;

    /**
     * The placeholder pattern.
     */
    private final Pattern pattern;

    /**
     * The function to retrieve the output of the placeholder.
     */
    private final Supplier<String> function;

    /**
     * The plugin for the placeholder.
     */
    private final EcoPlugin plugin;

    /**
     * Create a new player placeholder.
     *
     * @param plugin     The plugin.
     * @param identifier The identifier.
     * @param function   The function to retrieve the value.
     */
    public PlayerlessPlaceholder(@NotNull final EcoPlugin plugin,
                                 @NotNull final String identifier,
                                 @NotNull final Supplier<String> function) {
        this.plugin = plugin;
        this.identifier = identifier;
        this.pattern = Pattern.compile(identifier);
        this.function = function;
    }

    /**
     * Get the value of the placeholder.
     *
     * @return The value.
     */
    public String getValue() {
        return function.get();
    }

    /**
     * Register the placeholder.
     *
     * @return The placeholder.
     */
    public PlayerlessPlaceholder register() {
        PlaceholderManager.registerPlaceholder(this);
        return this;
    }

    @Override
    public @NotNull EcoPlugin getPlugin() {
        return this.plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return this.identifier;
    }

    @NotNull
    @Override
    public Pattern getPattern() {
        return this.pattern;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PlayerlessPlaceholder that)) {
            return false;
        }
        return Objects.equals(this.getIdentifier(), that.getIdentifier())
                && Objects.equals(this.getPlugin(), that.getPlugin());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getIdentifier(), this.getPlugin());
    }
}
