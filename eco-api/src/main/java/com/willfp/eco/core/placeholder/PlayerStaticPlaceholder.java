package com.willfp.eco.core.placeholder;

import com.willfp.eco.core.placeholder.context.PlaceholderContext;
import com.willfp.eco.util.PatternUtils;
import com.willfp.eco.util.StringUtils;
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
     * The identifier.
     */
    private final String identifier;

    /**
     * The arguments pattern.
     */
    private final Pattern pattern;

    /**
     * The function to retrieve the output of the arguments.
     */
    private final Function<@NotNull Player, @Nullable String> function;

    /**
     * Create a new player arguments.
     *
     * @param identifier The identifier.
     * @param function   The function to retrieve the value.
     */
    public PlayerStaticPlaceholder(@NotNull final String identifier,
                                   @NotNull final Function<@NotNull Player, @Nullable String> function) {
        this.identifier = "%" + identifier + "%";
        this.pattern = PatternUtils.compileLiteral(identifier);
        this.function = function;
    }

    @Override
    public @Nullable String getValue(@NotNull final String args,
                                     @NotNull final PlaceholderContext context) {
        Player player = context.getPlayer();

        if (player == null) {
            return null;
        }

        return this.getValue(player);
    }

    /**
     * Get the value of the arguments.
     *
     * @param player The player.
     * @return The value.
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
    public String tryTranslateQuickly(@NotNull final String text,
                                      @NotNull final PlaceholderContext context) {
        return StringUtils.replaceQuickly(
                text,
                this.identifier,
                Objects.requireNonNullElse(
                        this.getValue(identifier, context),
                        ""
                )
        );
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
        return Objects.equals(this.getPattern(), that.getPattern());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getPattern());
    }
}
