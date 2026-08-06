package com.willfp.eco.core.tuples;

import org.jetbrains.annotations.Nullable;

/**
 * Three values.
 *
 * @param <A> The first value type.
 * @param <B> The second value type.
 * @param <C> The third value type.
 */
public class Triplet<A, B, C> extends Pair<A, B> {
    /**
     * The third item in the tuple.
     */
    @Nullable
    private C third;

    /**
     * Create a triple of values.
     *
     * @param first  The first item in the triplet.
     * @param second The second item in the triplet.
     * @param third  The third item in the triplet.
     */
    public Triplet(@Nullable final A first,
                   @Nullable final B second,
                   @Nullable final C third) {
        super(first, second);

        this.third = third;
    }

    /**
     * Get the third member of the tuple, allowing the triplet to be destructured by kotlin.
     * <p>
     * The default kotlin triple already has this, however there is no default
     * triple in java so this exists for parity.
     *
     * @return The third member.
     */
    public C component3() {
        return third;
    }

    /**
     * Get the third member of the tuple.
     *
     * @return The third member.
     */
    public @Nullable C getThird() {
        return this.third;
    }

    /**
     * Set the third member of the tuple.
     *
     * @param third The data to set.
     */
    public void setThird(@Nullable final C third) {
        this.third = third;
    }
}
