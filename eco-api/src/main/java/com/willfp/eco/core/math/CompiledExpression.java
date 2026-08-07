package com.willfp.eco.core.math;

import org.jetbrains.annotations.NotNull;

/**
 * An expression that has been compiled once and can be evaluated many times.
 * <p>
 * Obtain instances from {@link ExpressionEnvironment#compile(String)}.
 * <p>
 * Implementations are safe for unlimited concurrent use.
 */
public interface CompiledExpression {
    /**
     * Evaluate the expression.
     * <p>
     * Values are positional, in the order the variable names were declared with
     * {@link ExpressionEnvironment.Builder#variables(String...)}.
     * <p>
     * Randomness is drawn from a default source backed by
     * {@link java.util.concurrent.ThreadLocalRandom}.
     *
     * @param values The variable values.
     * @return The result.
     * @throws IllegalArgumentException If fewer than {@link #getVariableCount()} values are given.
     */
    double evaluate(double... values);

    /**
     * Evaluate the expression, drawing any randomness from the given source.
     * <p>
     * Supply a seeded source to obtain reproducible results from expressions containing
     * {@code rand} or {@code random}. Custom functions cannot reach this source; a custom function
     * that needs randomness must close over its own and be registered as non-deterministic.
     *
     * @param random The random source.
     * @param values The variable values.
     * @return The result.
     * @throws IllegalArgumentException If fewer than {@link #getVariableCount()} values are given.
     */
    double evaluate(@NotNull RandomSource random,
                    double... values);

    /**
     * Get the number of variables this expression was compiled against.
     *
     * @return The variable count.
     */
    int getVariableCount();

    /**
     * Get whether this expression produces the same result for the same inputs.
     * <p>
     * False if the expression contains {@code rand} or {@code random}, or calls any custom function
     * registered as non-deterministic. True otherwise.
     *
     * @return True if deterministic.
     */
    boolean isDeterministic();
}
