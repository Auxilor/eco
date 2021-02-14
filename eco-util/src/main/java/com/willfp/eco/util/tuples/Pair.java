package com.willfp.eco.util.tuples;

import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class Pair<A, B> extends com.willfp.eco.util.tuplets.Pair<A, B> {
    /**
     * Create a pair of values.
     *
     * @param first  The first item in the pair.
     * @param second The second item in the pair.
     */
    public Pair(@Nullable final A first,
                @Nullable final B second) {
        super(first, second);
    }
}
