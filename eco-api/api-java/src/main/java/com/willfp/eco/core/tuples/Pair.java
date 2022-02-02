package com.willfp.eco.core.tuples;

import org.jetbrains.annotations.Nullable;

/**
 * Two values.
 *
 * @param <A> The first value type.
 * @param <B> The second value type.
 */
public class Pair<A, B> {
    /**
     * The first item in the tuple.
     */
    @Nullable
    private A first;

    /**
     * The second item in the tuple.
     */
    @Nullable
    private B second;

    /**
     * Create a pair of values.
     *
     * @param first  The first item in the pair.
     * @param second The second item in the pair.
     */
    public Pair(@Nullable final A first,
                @Nullable final B second) {
        this.first = first;
        this.second = second;
    }

    /**
     * component1 exists to allow a pair to be destructured by kotlin.
     * The default kotlin pair already has this, however there is no default
     * pair in java so this exists for parity.
     *
     * @return First.
     */
    public A component1() {
        return first;
    }

    /**
     * component2 exists to allow a pair to be destructured by kotlin.
     * The default kotlin pair already has this, however there is no default
     * pair in java so this exists for parity.
     *
     * @return First.
     */
    public B component2() {
        return second;
    }

    /**
     * Get the first member of the tuple.
     *
     * @return The first member.
     */
    public @Nullable A getFirst() {
        return this.first;
    }

    /**
     * Get the second member of the tuple.
     *
     * @return The second member.
     */
    public @Nullable B getSecond() {
        return this.second;
    }

    /**
     * Set the first member of the tuple.
     *
     * @param first The data to set.
     */
    public void setFirst(@Nullable final A first) {
        this.first = first;
    }

    /**
     * Set the second member of the tuple.
     *
     * @param second The data to set.
     */
    public void setSecond(@Nullable final B second) {
        this.second = second;
    }
}
