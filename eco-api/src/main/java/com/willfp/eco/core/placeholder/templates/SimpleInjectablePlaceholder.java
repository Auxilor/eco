package com.willfp.eco.core.placeholder.templates;

import com.willfp.eco.core.placeholder.InjectablePlaceholder;
import com.willfp.eco.core.placeholder.context.PlaceholderContext;
import com.willfp.eco.util.PatternUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * A template class for simple placeholders.
 */
public abstract class SimpleInjectablePlaceholder implements InjectablePlaceholder {
    /**
     * The name of the placeholder.
     */
    private final String identifier;

    /**
     * The placeholder pattern.
     */
    private final Pattern pattern;

    /**
     * Create a new simple injectable placeholder.
     *
     * @param identifier The identifier.
     */
    protected SimpleInjectablePlaceholder(@NotNull final String identifier) {
        this.identifier = identifier;
        this.pattern = PatternUtils.compileLiteral(identifier);
    }

    @Override
    public String tryTranslateQuickly(@NotNull final String text,
                                      @NotNull final PlaceholderContext context) {
        return text.replace(
                "%" + this.identifier + "%",
                Objects.requireNonNullElse(this.getValue(this.identifier, context), "")
        );
    }

    @Override
    public @NotNull Pattern getPattern() {
        return pattern;
    }

    @Override
    public String toString() {
        return "SimpleInjectablePlaceholder{" +
                "identifier='" + identifier + '\'' +
                '}';
    }

    @Override
    public boolean equals(@NotNull final Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof SimpleInjectablePlaceholder that)) {
            return false;
        }

        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }
}
