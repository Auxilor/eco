package com.willfp.eco.core.lookup;

import com.willfp.eco.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Parse a key into segments.
 */
public abstract class SegmentParser {
    /**
     * All segment parsers.
     */
    private static final List<SegmentParser> REGISTERED = new ArrayList<>();

    /**
     * The pattern to split keys on.
     */
    private final String pattern;

    /**
     * Create new lookup segment parser.
     *
     * @param pattern The pattern.
     */
    protected SegmentParser(@NotNull final String pattern) {
        this.pattern = pattern;
    }

    /**
     * Register the parser.
     *
     * @return The parser.
     */
    public SegmentParser register() {
        REGISTERED.add(this);
        return this;
    }

    /**
     * Try parse segments from key.
     *
     * @param key     The key.
     * @param handler The handler.
     * @param <T>     The object type.
     * @return Null if no segments were found, or the object generated from the segments.
     */
    @Nullable
    public <T extends Testable<?>> T parse(@NotNull final String key,
                                           @NotNull final LookupHandler<T> handler) {
        if (!key.contains(" " + pattern + " ")) {
            return null;
        }

        String[] segments = StringUtils.splitAround(key, pattern);

        return handleSegments(segments, handler);
    }

    /**
     * Handle segments from key.
     *
     * @param segments The key segments.
     * @param handler  The handler.
     * @param <T>      The object type.
     * @return The returned object.
     */
    protected abstract <T extends Testable<?>> T handleSegments(@NotNull String[] segments,
                                                                @NotNull LookupHandler<T> handler);

    /**
     * Get all segment parsers.
     *
     * @return All parsers.
     */
    public static Collection<SegmentParser> values() {
        return new ArrayList<>(REGISTERED);
    }
}
