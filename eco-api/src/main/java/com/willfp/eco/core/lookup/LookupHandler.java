package com.willfp.eco.core.lookup;

import com.willfp.eco.util.StringUtils;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

/**
 * Handle lookups, used in {@link com.willfp.eco.core.entities.Entities} and {@link com.willfp.eco.core.items.Items}.
 *
 * @param <T> The type of testable object, eg {@link com.willfp.eco.core.items.TestableItem}.
 */
public interface LookupHandler<T extends Testable<?>> {
    /**
     * Parse lookup string completely.
     * <p>
     * You shouldn't override this method unless you're doing something
     * technically interesting or weird. This is the entry point for all
     * lookup parsers, {@link #parse(String[])} is to specify implementation-specific
     * parsing.
     * <p>
     * Every registered {@link SegmentParser} is tried first; if none of them match, the
     * key is tokenised and handed to {@link #parse(String[])}.
     *
     * @param key The key.
     * @return The object, or the failsafe if the key could not be parsed.
     */
    default T parseKey(@NotNull String key) {
        for (SegmentParser parser : SegmentParser.values()) {
            T generated = parser.parse(key, this);

            if (generated != null) {
                return generated;
            }
        }

        String[] args = StringUtils.parseTokens(key);

        return this.parse(args);
    }

    /**
     * Parse arguments to an object.
     *
     * @param args The arguments.
     * @return The object, or the failsafe if the arguments could not be parsed.
     */
    @NotNull
    T parse(@NotNull String[] args);

    /**
     * Validate an object.
     * <p>
     * Should return false for the failsafe object.
     *
     * @param object The object.
     * @return If validated.
     */
    boolean validate(@NotNull T object);

    /**
     * Get the failsafe object.
     * <p>
     * A failsafe object should never pass validate(), as this will
     * cause issues with segment parsers. See {@link com.willfp.eco.core.items.ItemsLookupHandler} and
     * {@link com.willfp.eco.core.recipe.parts.EmptyTestableItem} for examples.
     *
     * @return The failsafe.
     */
    @NotNull
    T getFailsafe();

    /**
     * Join several options together.
     *
     * @param options The options.
     * @return The joined object.
     */
    @NotNull
    T join(@NotNull Collection<T> options);
}
