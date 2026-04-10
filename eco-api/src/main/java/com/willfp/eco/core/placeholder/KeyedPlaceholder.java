package com.willfp.eco.core.placeholder;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.placeholder.context.PlaceholderContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A general-purpose placeholder type that handles an entire family of
 * identifiers through an ordered list of deserializers and a set of
 * attributes.
 * <p>
 * Example: a placeholder of the form
 * {@code guild_<guildId>_member_<playerName>_rank_<rankId>_<attribute>}
 * is handled by a single {@code KeyedPlaceholder} with three deserializers
 * and a set of attributes.
 * <p>
 * Parsing proceeds right-to-left: the attribute suffix is matched and
 * stripped from the tail, then each deserializer peels
 * {@code _literal_value} (or {@code literal_value} for the leftmost) from
 * the tail in reverse order, validating the value against the
 * deserializer's pattern. Resolvers then execute front-to-back, each
 * receiving the list of previously resolved domain objects. The
 * attribute's value function dispatches on the full chain.
 * <p>
 * Instances are constructed via {@link #builder(EcoPlugin)} and
 * registered through {@link #register()}.
 * <p>
 * Instances are immutable after construction and safe to use from
 * any thread.
 */
public final class KeyedPlaceholder implements RegistrablePlaceholder {
    private final EcoPlugin plugin;
    private final List<KeyedPlaceholderDeserializer> deserializers;
    private final Map<String, KeyedPlaceholderAttribute> attributesByName;
    private final List<String> attributeNamesLongestFirst;
    private final Pattern gatePattern;

    // Per-deserializer precompiled patterns, parallel to `deserializers`.
    // tailPatterns[i] matches "_<literal>_(<valuePattern>)$" for non-leftmost.
    // leftmostPattern matches the full remainder for deserializer 0.
    private final Pattern[] tailPatterns;
    private final Pattern leftmostPattern;

    private KeyedPlaceholder(
            @NotNull final EcoPlugin plugin,
            @NotNull final List<KeyedPlaceholderDeserializer> deserializers,
            @NotNull final LinkedHashMap<String, KeyedPlaceholderAttribute> attributesByName
    ) {
        if (deserializers.isEmpty()) {
            throw new IllegalArgumentException("KeyedPlaceholder requires at least one deserializer");
        }
        if (attributesByName.isEmpty()) {
            throw new IllegalArgumentException("KeyedPlaceholder requires at least one attribute");
        }
        for (int i = 0; i < deserializers.size(); i++) {
            KeyedPlaceholderDeserializer d = deserializers.get(i);
            if (d.getLiteral().isEmpty() && i != 0) {
                throw new IllegalArgumentException(
                        "Only the leftmost deserializer may have an empty literal (index "
                                + i + " has empty literal)"
                );
            }
        }

        this.plugin = plugin;
        this.deserializers = List.copyOf(deserializers);
        this.attributesByName = Collections.unmodifiableMap(new LinkedHashMap<>(attributesByName));

        // Sort attribute names by length descending for longest-match-first parsing.
        List<String> sortedNames = new ArrayList<>(attributesByName.keySet());
        sortedNames.sort(Comparator.comparingInt(String::length).reversed());
        this.attributeNamesLongestFirst = Collections.unmodifiableList(sortedNames);

        this.gatePattern = compileGatePattern(this.deserializers, sortedNames);

        // Precompile per-deserializer patterns.
        this.tailPatterns = new Pattern[this.deserializers.size()];
        for (int i = 1; i < this.deserializers.size(); i++) {
            KeyedPlaceholderDeserializer d = this.deserializers.get(i);
            String expected = "_" + Pattern.quote(d.getLiteral()) + "_(" + d.getValuePatternSource() + ")$";
            this.tailPatterns[i] = Pattern.compile(expected);
        }
        KeyedPlaceholderDeserializer leftmost = this.deserializers.getFirst();
        String leftmostExpected;
        if (leftmost.getLiteral().isEmpty()) {
            leftmostExpected = "^(" + leftmost.getValuePatternSource() + ")$";
        } else {
            leftmostExpected = "^" + Pattern.quote(leftmost.getLiteral()) + "_(" + leftmost.getValuePatternSource() + ")$";
        }
        this.leftmostPattern = Pattern.compile(leftmostExpected);
    }

    /**
     * Compile the gate pattern used by {@link #getPattern()}.
     * <p>
     * The gate pattern matches any identifier this placeholder will
     * successfully parse. It is used by
     * {@code PlaceholderLookup.findMatchingPlaceholder} to short-circuit
     * non-matching identifiers before {@link #getValue} is called.
     * Structural parsing in {@link #getValue} handles the actual dispatch.
     * <p>
     * Pattern shape:
     * {@code ^<lit0>_<val0>(_<lit1>_<val1>)...(_<litN>_<valN>)_(<attr1>|<attr2>|...)$}
     * <p>
     * If the leftmost literal is empty, the pattern starts with just
     * {@code <val0>} (no leading literal or underscore).
     */
    @NotNull
    private static Pattern compileGatePattern(
            @NotNull final List<KeyedPlaceholderDeserializer> deserializers,
            @NotNull final List<String> attributeNames
    ) {
        StringBuilder sb = new StringBuilder();
        sb.append("^");
        for (int i = 0; i < deserializers.size(); i++) {
            KeyedPlaceholderDeserializer d = deserializers.get(i);
            if (i == 0) {
                if (!d.getLiteral().isEmpty()) {
                    sb.append(Pattern.quote(d.getLiteral()));
                    sb.append("_");
                }
                sb.append("(?:").append(d.getValuePatternSource()).append(")");
            } else {
                sb.append("_");
                sb.append(Pattern.quote(d.getLiteral()));
                sb.append("_");
                sb.append("(?:").append(d.getValuePatternSource()).append(")");
            }
        }
        sb.append("_(?:");
        for (int i = 0; i < attributeNames.size(); i++) {
            if (i > 0) sb.append("|");
            sb.append(Pattern.quote(attributeNames.get(i)));
        }
        sb.append(")$");
        return Pattern.compile(sb.toString());
    }

    @Override
    @NotNull
    public EcoPlugin getPlugin() {
        return plugin;
    }

    @Override
    @NotNull
    public Pattern getPattern() {
        return gatePattern;
    }

    @Override
    @Nullable
    public String getValue(@NotNull final String args, @NotNull final PlaceholderContext context) {
        // Phase 1: strip attribute from the tail (longest match first).
        String remaining = args;
        KeyedPlaceholderAttribute matchedAttribute = null;
        for (String attrName : attributeNamesLongestFirst) {
            String suffix = "_" + attrName;
            if (remaining.endsWith(suffix)) {
                matchedAttribute = attributesByName.get(attrName);
                remaining = remaining.substring(0, remaining.length() - suffix.length());
                break;
            }
        }
        if (matchedAttribute == null) {
            return null;
        }

        // Phase 2: peel deserializers right-to-left.
        String[] chunks = new String[deserializers.size()];
        for (int i = deserializers.size() - 1; i >= 1; i--) {
            Matcher m = tailPatterns[i].matcher(remaining);
            if (!m.find()) {
                return null;
            }
            chunks[i] = m.group(1);
            remaining = remaining.substring(0, m.start());
        }
        // Leftmost deserializer matches the full remainder.
        Matcher leftmostMatcher = leftmostPattern.matcher(remaining);
        if (!leftmostMatcher.matches()) {
            return null;
        }
        chunks[0] = leftmostMatcher.group(1);

        // Phase 3: execute resolvers front-to-back.
        List<Object> resolved = new ArrayList<>(deserializers.size());
        for (int i = 0; i < deserializers.size(); i++) {
            KeyedPlaceholderDeserializer d = deserializers.get(i);
            Object value = d.getResolver().apply(Collections.unmodifiableList(resolved), chunks[i]);
            if (value == null) {
                return null;
            }
            resolved.add(value);
        }

        // Phase 4: dispatch attribute.
        return matchedAttribute.valueFunction().apply(Collections.unmodifiableList(resolved), context);
    }

    @Override
    @NotNull
    public KeyedPlaceholder register() {
        return (KeyedPlaceholder) RegistrablePlaceholder.super.register();
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) return true;
        if (!(o instanceof KeyedPlaceholder that)) return false;
        return Objects.equals(this.gatePattern.pattern(), that.gatePattern.pattern())
                && Objects.equals(this.plugin, that.plugin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gatePattern.pattern(), plugin);
    }

    /**
     * Start building a new {@link KeyedPlaceholder}.
     *
     * @param plugin The plugin that owns this placeholder.
     * @return A new builder.
     */
    @NotNull
    public static Builder builder(@NotNull final EcoPlugin plugin) {
        return new Builder(plugin);
    }

    /**
     * Builder for {@link KeyedPlaceholder}.
     * <p>
     * Use {@link #key(String, String, BiFunction)} to add deserializers
     * in order from leftmost to rightmost. Use
     * {@link #attribute(String, BiFunction)} to add attributes. Call
     * {@link #register()} (or {@link #build()} followed by
     * {@link KeyedPlaceholder#register()}) to finalize.
     */
    public static final class Builder {
        private final EcoPlugin plugin;
        private final List<KeyedPlaceholderDeserializer> deserializers = new ArrayList<>();
        private final LinkedHashMap<String, KeyedPlaceholderAttribute> attributes = new LinkedHashMap<>();

        private Builder(@NotNull final EcoPlugin plugin) {
            this.plugin = plugin;
        }

        /**
         * Add a key deserializer. Call in order from leftmost to rightmost.
         *
         * @param literal      The literal token. Use "" (empty string) only
         *                     for the leftmost deserializer when the shape
         *                     has no prefix.
         * @param valuePattern The regex pattern for valid key values.
         *                     Must not contain anchors or capture groups.
         * @param resolver     The resolver function.
         * @return This builder.
         */
        @NotNull
        public Builder key(
                @NotNull final String literal,
                @NotNull final String valuePattern,
                @NotNull final BiFunction<@NotNull List<@NotNull Object>, @NotNull String, @Nullable Object> resolver
        ) {
            deserializers.add(new KeyedPlaceholderDeserializer(literal, valuePattern, resolver));
            return this;
        }

        /**
         * Add an attribute.
         *
         * @param name          The attribute name (suffix). May contain
         *                      underscores. Must not be empty.
         * @param valueFunction The value function.
         * @return This builder.
         */
        @NotNull
        public Builder attribute(
                @NotNull final String name,
                @NotNull final BiFunction<@NotNull List<@NotNull Object>, @NotNull PlaceholderContext, @Nullable String> valueFunction
        ) {
            attributes.put(name, new KeyedPlaceholderAttribute(name, valueFunction));
            return this;
        }

        /**
         * Build the {@link KeyedPlaceholder} without registering it.
         *
         * @return The new placeholder.
         */
        @NotNull
        public KeyedPlaceholder build() {
            return new KeyedPlaceholder(plugin, deserializers, attributes);
        }

        /**
         * Build and register the {@link KeyedPlaceholder}.
         *
         * @return The registered placeholder.
         */
        @NotNull
        public KeyedPlaceholder register() {
            return build().register();
        }
    }
}