package com.willfp.eco.lookup;

import com.willfp.eco.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Parse a key into segments.
 */
public abstract class SegmentParser {
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
     * Handle segments from key.
     *
     * @param segments The key segments.
     * @param handler  The handler.
     * @param <T>      The object type.
     * @return The returned object.
     */
    protected abstract <T> T handleSegments(@NotNull String[] segments,
                                            @NotNull LookupHandler<T> handler);

    /**
     * Try parse segments from key.
     *
     * @param key     The key.
     * @param handler The handler.
     * @param <T>     The object type.
     * @return Null if no segments were found, or the object generated from the segments.
     */
    @Nullable
    public <T> T parse(@NotNull final String key,
                       @NotNull final LookupHandler<T> handler) {
        if (!key.contains(" " + pattern + " ")) {
            return null;
        }

        String[] segments = StringUtils.splitAround(key, pattern);

        return handleSegments(segments, handler);
    }
}
