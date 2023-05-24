package com.willfp.eco.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Utilities / API methods for patterns.
 */
public final class PatternUtils {
    /**
     * Cache of compiled literal patterns.
     */
    private static final Cache<String, Pattern> LITERAL_PATTERN_CACHE = Caffeine.newBuilder()
            .expireAfterAccess(1, TimeUnit.MINUTES)
            .build();

    /**
     * Compile a literal pattern.
     *
     * @param pattern The pattern.
     * @return The compiled pattern.
     */
    @NotNull
    public static Pattern compileLiteral(@NotNull final String pattern) {
        return LITERAL_PATTERN_CACHE.get(pattern, (it) -> Pattern.compile(it, Pattern.LITERAL));
    }

    private PatternUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
