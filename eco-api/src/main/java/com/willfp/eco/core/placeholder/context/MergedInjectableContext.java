package com.willfp.eco.core.placeholder.context;

import com.willfp.eco.core.placeholder.InjectablePlaceholder;
import com.willfp.eco.core.placeholder.PlaceholderInjectable;
import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link PlaceholderInjectable} that merges the injections of two other contexts.
 * <p>
 * Placeholders added to this context are stored separately, and are returned after the injections of the base and
 * additional contexts. Clearing this context also clears both underlying contexts.
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
     * Extra injections added directly to this context.
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
