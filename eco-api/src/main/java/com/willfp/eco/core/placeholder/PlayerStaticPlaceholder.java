package com.willfp.eco.core.placeholder;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * A placeholder that cannot be registered, and exists purely in injection.
 */
public final class PlayerStaticPlaceholder implements InjectablePlaceholder {
    /**
     * The name of the placeholder.
     */
    private final String identifier;

    /**
     * The placeholder pattern.
     */
    private final Pattern pattern;

    /**
     * The function to retrieve the output of the placeholder.
     */
    private final Function<Player, String> function;

    /**
     * Create a new player placeholder.
     *
     * @param identifier The identifier.
     * @param function   The function to retrieve the value.
     */
    public PlayerStaticPlaceholder(@NotNull final String identifier,
                                   @NotNull final Function<Player, String> function) {
        this.identifier = identifier;
        this.pattern = Pattern.compile(identifier);
        this.function = function;
    }

    /**
     * Get the value of the placeholder.
     *
     * @param player The player.
     * @return The value.
     */
    @NotNull
    public String getValue(@NotNull final Player player) {
        return function.apply(player);
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
        if (!(o instanceof PlayerStaticPlaceholder that)) {
            return false;
        }
        return Objects.equals(this.getIdentifier(), that.getIdentifier());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getIdentifier());
    }
}
