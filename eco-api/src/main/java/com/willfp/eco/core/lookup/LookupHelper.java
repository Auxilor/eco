package com.willfp.eco.core.lookup;

import com.willfp.eco.core.lookup.test.Testable;
import com.willfp.eco.util.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper for lookups.
 */
public final class LookupHelper {
    /**
     * All segment parsers.
     */
    private static final List<SegmentParser> SEGMENT_PARSERS = new ArrayList<>();

    /**
     * Parse key to object.
     *
     * @param key     The key.
     * @param handler The handler.
     * @param <T>     The object type.
     * @return The object.
     */
    @NotNull
    public static <T extends Testable<?>> T parseWith(@NotNull final String key,
                                                      @NotNull final LookupHandler<T> handler) {
        for (SegmentParser parser : SEGMENT_PARSERS) {
            T generated = parser.parse(key, handler);

            if (generated != null) {
                return generated;
            }
        }

        String[] args = StringUtils.parseTokens(key);

        return handler.parse(args);
    }

    /**
     * Register segment parser.
     *
     * @param parser The parser.
     */
    public static void registerSegmentParser(@NotNull final SegmentParser parser) {
        SEGMENT_PARSERS.add(parser);
    }

    private LookupHelper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
