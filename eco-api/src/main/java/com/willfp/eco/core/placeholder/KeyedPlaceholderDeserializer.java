package com.willfp.eco.core.placeholder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

/**
 * A single key-step configuration for a {@link KeyedPlaceholder}.
 * <p>
 * Each deserializer declares:
 * <ul>
 *     <li>A literal token (e.g. "quest", "task", "member"). May be empty
 *         for the leftmost deserializer only, indicating a prefixless form.</li>
 *     <li>A value pattern — a regex describing the shape of this key's
 *         string form (e.g. "[a-z0-9_]+", "[0-9]+"). Must not contain
 *         anchors (^, $) or capture groups.</li>
 *     <li>A resolver function taking the list of previously resolved
 *         domain objects and the parsed string chunk for this key,
 *         returning a domain object or null.</li>
 * </ul>
 * <p>
 * Instances are immutable and safe to share across threads.
 */
public final class KeyedPlaceholderDeserializer {
    private final String literal;
    private final String valuePatternSource;
    private final Pattern valuePattern;
    private final BiFunction<@NotNull List<@NotNull Object>, @NotNull String, @Nullable Object> resolver;

    /**
     * Create a new deserializer.
     *
     * @param literal      The literal token (e.g. "quest"). Empty strings are
     *                     permitted only for the leftmost deserializer.
     * @param valuePattern The regex pattern describing valid key strings.
     *                     Must not contain anchors or capture groups.
     * @param resolver     The resolver function. Receives the list of
     *                     previously resolved objects (empty for the
     *                     leftmost deserializer) and the parsed chunk
     *                     for this key. Returns the resolved domain
     *                     object or null if resolution fails.
     */
    public KeyedPlaceholderDeserializer(
            @NotNull final String literal,
            @NotNull final String valuePattern,
            @NotNull final BiFunction<@NotNull List<@NotNull Object>, @NotNull String, @Nullable Object> resolver
    ) {
        this.literal = literal;
        this.valuePatternSource = valuePattern;
        this.valuePattern = Pattern.compile(valuePattern);
        this.resolver = resolver;
    }

    @NotNull
    public String getLiteral() {
        return literal;
    }

    @NotNull
    public String getValuePatternSource() {
        return valuePatternSource;
    }

    @NotNull
    public Pattern getValuePattern() {
        return valuePattern;
    }

    @NotNull
    public BiFunction<@NotNull List<@NotNull Object>, @NotNull String, @Nullable Object> getResolver() {
        return resolver;
    }
}