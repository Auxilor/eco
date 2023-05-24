package com.willfp.eco.core.placeholder;

import com.willfp.eco.core.placeholder.context.PlaceholderContext;
import com.willfp.eco.util.PatternUtils;
import com.willfp.eco.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * A arguments that cannot be registered, and exists purely in injection.
 */
public final class StaticPlaceholder implements InjectablePlaceholder {
    /**
     * The name of the arguments.
     */
    private final String identifier;

    /**
     * The arguments pattern.
     */
    private final Pattern pattern;

    /**
     * The function to retrieve the output of the arguments.
     */
    private final Supplier<@Nullable String> function;

    /**
     * Create a new player arguments.
     *
     * @param identifier The identifier.
     * @param function   The function to retrieve the value.
     */
    public StaticPlaceholder(@NotNull final String identifier,
                             @NotNull final Supplier<@Nullable String> function) {
        this.identifier = "%" + identifier + "%";
        this.pattern = PatternUtils.compileLiteral(identifier);
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
                function.get(),
                ""
        );
    }

    @Override
    public String tryTranslateQuickly(@NotNull final String text,
                                      @NotNull final PlaceholderContext context) {
        return StringUtils.replaceQuickly(
                text,
                this.identifier,
                Objects.requireNonNullElse(this.getValue(this.identifier, context), "")
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
        if (!(o instanceof StaticPlaceholder that)) {
            return false;
        }
        return Objects.equals(this.getPattern(), that.getPattern());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getPattern());
    }
}
