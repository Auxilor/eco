package com.willfp.eco.util;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.cache.EcoCache;
import java.time.Duration;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

/**
 * Utilities / API methods for patterns.
 */
public final class PatternUtils {
    /**
     * Cache of compiled literal patterns, expiring after the number of minutes configured under
     * {@code literal-cache-ttl} in eco's config since the pattern was last used.
     */
    private static final EcoCache<String, Pattern> LITERAL_PATTERN_CACHE = EcoCache.<String, Pattern>builder()
            .expireAfterAccess(Duration.ofMinutes(Eco.get().getEcoPlugin().getConfigYml().getInt("literal-cache-ttl")))
            .build();

    /**
     * Compile a literal pattern.
     * <p>
     * The string is compiled with {@link Pattern#LITERAL}, so regex metacharacters in it carry no
     * special meaning. Compiled patterns are cached and reused, so this is cheaper than calling
     * {@link Pattern#compile(String, int)} repeatedly with the same string.
     *
     * @param pattern The literal string to match.
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
