package com.willfp.eco.core.tuples;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

public class Triplet<A, B, C> extends Pair<A, B> {
    /**
     * The third item in the tuple.
     */
    @Getter
    @Setter
    @Nullable
    private C third;

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
        super(first, second);

        this.third = third;
    }
}
