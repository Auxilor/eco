package com.willfp.eco.core.math;

/**
 * A source of pseudorandom numbers used when evaluating a {@link CompiledExpression}.
 * <p>
 * Supply a seeded implementation to obtain reproducible results from expressions containing
 * {@code rand} or {@code random}.
 */
@FunctionalInterface
public interface RandomSource {
    /**
     * Get the next pseudorandom value.
     * <p>
     * Implementations must return a value in the range {@code [0, 1)}. Implementations need not be
     * thread-safe; a caller that shares a stateful source across threads is responsible for the
     * consequences, and reproducibility is not guaranteed in that case.
     *
     * @return The next value, in {@code [0, 1)}.
     */
    double nextDouble();
}
