package com.willfp.eco.core.placeholder.context;

import com.willfp.eco.core.placeholder.InjectablePlaceholder;
import com.willfp.eco.core.placeholder.PlaceholderInjectable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * A merged injectable context.
 */
public class MergedInjectableContext implements PlaceholderInjectable {
    /**
     * The base context.
     */
    private final PlaceholderInjectable baseContext;

    /**
     * The additional context.
     */
    private final PlaceholderInjectable additionalContext;

    /**
     * Extra injections.
     */
    private final Set<InjectablePlaceholder> extraInjections = new HashSet<>();

    /**
     * Create a new merged injectable context.
     *
     * @param baseContext       The base context.
     * @param additionalContext The additional context.
     */
    public MergedInjectableContext(@NotNull final PlaceholderInjectable baseContext,
                                   @NotNull final PlaceholderInjectable additionalContext) {
        this.baseContext = baseContext;
        this.additionalContext = additionalContext;
    }

    @Override
    public void addInjectablePlaceholder(@NotNull final Iterable<InjectablePlaceholder> placeholders) {
        for (InjectablePlaceholder placeholder : placeholders) {
            extraInjections.add(placeholder);
        }
    }

    @Override
    public void clearInjectedPlaceholders() {
        baseContext.clearInjectedPlaceholders();
        additionalContext.clearInjectedPlaceholders();
        extraInjections.clear();
    }

    @Override
    public @NotNull List<InjectablePlaceholder> getPlaceholderInjections() {
        List<InjectablePlaceholder> base = baseContext.getPlaceholderInjections();
        List<InjectablePlaceholder> additional = additionalContext.getPlaceholderInjections();

        List<InjectablePlaceholder> injections = new ArrayList<>(base.size() + additional.size() + extraInjections.size());

        injections.addAll(base);
        injections.addAll(additional);
        injections.addAll(extraInjections);

        return injections;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof MergedInjectableContext that)) {
            return false;
        }

        return Objects.equals(baseContext, that.baseContext)
                && Objects.equals(additionalContext, that.additionalContext)
                && Objects.equals(extraInjections, that.extraInjections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseContext, additionalContext, extraInjections);
    }
}
