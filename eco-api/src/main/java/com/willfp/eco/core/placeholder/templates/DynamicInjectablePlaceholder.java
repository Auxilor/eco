package com.willfp.eco.core.placeholder.templates;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.placeholder.InjectablePlaceholder;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * A template class for dynamic placeholders.
 */
public abstract class DynamicInjectablePlaceholder implements InjectablePlaceholder {

    /**
     * The placeholder pattern.
     */
    private final Pattern pattern;

    /**
     * Create a new dynamic injectable placeholder.
     *
     * @param pattern The pattern.
     */
    protected DynamicInjectablePlaceholder(@NotNull final Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public @NotNull Pattern getPattern() {
        return pattern;
    }

    @Override
    public String toString() {
        return "DynamicInjectablePlaceholder{" +
                "pattern='" + pattern + '\'' +
                '}';
    }

    @Override
    public boolean equals(@NotNull final Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof DynamicInjectablePlaceholder that)) {
            return false;
        }

        return Objects.equals(pattern, that.getPattern())
                && Objects.equals(plugin, that.getPlugin());
    }

    @Override
    public int hashCode() {
        return Objects.hash(pattern, plugin);
    }
}
