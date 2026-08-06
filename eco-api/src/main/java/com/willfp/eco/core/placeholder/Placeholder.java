package com.willfp.eco.core.placeholder;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.placeholder.context.PlaceholderContext;

import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A placeholder represents a string that can hold a value.
 */
public interface Placeholder {
    /**
     * Get the plugin that owns the placeholder.
     *
     * @return The plugin, or null if the placeholder is not owned by a plugin.
     */
    @Nullable
    EcoPlugin getPlugin();

    /**
     * Get the value of the placeholder.
     *
     * @param args    The args, i.e. the text matched by {@link #getPattern()}.
     * @param context The context to resolve the value in.
     * @return The value, or null if the placeholder could not be resolved.
     */
    @Nullable
    String getValue(@NotNull String args,
                    @NotNull PlaceholderContext context);

    /**
     * Get the pattern that this placeholder matches.
     *
     * @return The pattern.
     */
    @NotNull
    Pattern getPattern();

    /**
     * Get the pattern string.
     * <p>
     * This method is available to allow for greater performance where the pattern string can be accessed without
     * compiling the regex.
     *
     * @return The pattern string.
     */
    @NotNull
    default String getPatternString() {
        return this.getPattern().pattern();
    }

    /**
     * Try to translate all instances of this placeholder in text quickly.
     * <p>
     * The default implementation performs no translation and returns the text unchanged; placeholders that can be
     * matched without running the regex should override this.
     *
     * @param text    The text to translate.
     * @param context The context to resolve the value in.
     * @return The translated text.
     */
    default String tryTranslateQuickly(@NotNull final String text,
                                       @NotNull final PlaceholderContext context) {
        return text;
    }

    /**
     * Get the identifier for the placeholder.
     *
     * @return The identifier, which defaults to the pattern string.
     * @deprecated Some placeholders may not have an identifier. Use {@link #getPattern()} instead.
     */
    @Deprecated(since = "6.56.0", forRemoval = true)
    @NotNull
    default String getIdentifier() {
        return this.getPattern().pattern();
    }
}
