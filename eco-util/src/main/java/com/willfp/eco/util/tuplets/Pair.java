package com.willfp.eco.util.tuplets;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("DeprecatedIsStillUsed")
@ToString
@Deprecated
public class Pair<A, B> {
    /**
     * The first value in the pair.
     */
    @Getter
    @Setter
    @Nullable
    private A first;

    /**
     * The second value in the pair.
     */
    @Getter
    @Setter
    @Nullable
    private B second;

    /**
     * Create a pair of values.
     * <p>
     * This is deprecated because I forgot how to spell Tuples before putting this into production.
     *
     * @param first  The first item in the pair.
     * @param second The second item in the pair.
     */
    public Pair(@Nullable final A first,
                @Nullable final B second) {
        this.first = first;
        this.second = second;
    }
}
