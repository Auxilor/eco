package com.willfp.eco.util.tuples;

import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class Triplet<A, B, C> extends com.willfp.eco.util.tuplets.Triplet<A, B, C> {
    /**
     * Create a triple of values.
     *
     * @param first  The first item in the pair.
     * @param second The second item in the pair.
     * @param third  The third item in the pair.
     */
    public Triplet(@Nullable final A first,
                   @Nullable final B second,
                   @Nullable final C third) {
        super(first, second, third);
    }
}
