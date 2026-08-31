package com.willfp.eco.core.lookup;

import com.willfp.eco.util.StringUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Parse a key into segments.
 * <p>
 * A segment parser splits a lookup key around a pattern (for example {@code and} or
 * {@code or}), parses each segment with the {@link LookupHandler}, and combines the
 * results. Parsers must be registered with {@link #register()} to be used by
 * {@link LookupHandler#parseKey(String)}.
 */
public abstract class SegmentParser {
    /**
     * All registered segment parsers.
     */
    private static final List<SegmentParser> REGISTERED = new CopyOnWriteArrayList<>();

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
     * @return This parser, for chaining.
     */
    public SegmentParser register() {
        REGISTERED.add(this);
        return this;
    }

    /**
     * Try parse segments from key.
     * <p>
     * Only splits if the key contains the pattern surrounded by spaces.
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
     * @return The object generated from the segments.
     */
    protected abstract <T extends Testable<?>> T handleSegments(@NotNull String[] segments,
                                                                @NotNull LookupHandler<T> handler);

    /**
     * Get all registered segment parsers.
     *
     * @return A copy of all registered parsers.
     */
    public static Collection<SegmentParser> values() {
        return new ArrayList<>(REGISTERED);
    }
}
