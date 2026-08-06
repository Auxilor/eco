package com.willfp.eco.core.placeholder;

import com.willfp.eco.core.placeholder.context.PlaceholderContext;
import com.willfp.eco.util.PatternUtils;
import com.willfp.eco.util.StringUtils;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A placeholder that cannot be registered, and exists purely in injection.
 */
public final class StaticPlaceholder implements InjectablePlaceholder {
    /**
     * The identifier, wrapped in percent signs, e.g. "%identifier%".
     */
    private final String identifier;

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
     * The function to retrieve the value of the placeholder.
     */
    private final Supplier<@Nullable String> function;

    /**
     * Create a new static placeholder.
     *
     * @param identifier The identifier.
     * @param function   The function to retrieve the value.
     */
    public StaticPlaceholder(@NotNull final String identifier,
                             @NotNull final Supplier<@Nullable String> function) {
        this.identifier = "%" + identifier + "%";
        this.rawIdentifier = identifier;
        this.function = function;
    }

    @Override
    public @Nullable String getValue(@NotNull final String args,
                                     @NotNull final PlaceholderContext context) {
        return function.get();
    }

    /**
     * Get the value of the placeholder.
     *
     * @return The value, or an empty string if the supplier returned null.
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

    @NotNull
    @Override
    public String getPatternString() {
        return this.rawIdentifier;
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
        return Objects.hash(this.getPatternString());
    }
}
