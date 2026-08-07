package com.willfp.eco.core.math;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.ToDoubleFunction;

/**
 * A set of variables and functions against which expressions can be compiled.
 * <p>
 * Create one with {@code Eco.get().createExpressionEnvironmentBuilder()}.
 * <p>
 * Environments are immutable once built and safe to share across threads.
 *
 * <pre>{@code
 * ExpressionEnvironment env = Eco.get().createExpressionEnvironmentBuilder()
 *         .variables("x", "y", "z")
 *         .function("noise3", 3, args -> noise.get(args[0], args[1], args[2]))
 *         .build();
 *
 * CompiledExpression expr = env.compileOrThrow("noise3(x, y, z) * 0.5 + clamp(y / 64, 0, 1)");
 *
 * double result = expr.evaluate(x, y, z);
 * }</pre>
 */
public interface ExpressionEnvironment {
    /**
     * Compile an expression, returning null if it is invalid.
     *
     * @param expression The expression.
     * @return The compiled expression, or null if it failed to compile.
     */
    @Nullable
    CompiledExpression compile(@NotNull String expression);

    /**
     * Compile an expression, throwing if it is invalid.
     * <p>
     * Prefer this when failure should be loud, for example when validating configuration at load
     * time.
     *
     * @param expression The expression.
     * @return The compiled expression.
     * @throws ExpressionCompilationException If the expression failed to compile.
     */
    @NotNull
    CompiledExpression compileOrThrow(@NotNull String expression) throws ExpressionCompilationException;

    /**
     * Builds {@link ExpressionEnvironment}s.
     * <p>
     * Builders are mutable and are not safe for concurrent use.
     */
    interface Builder {
        /**
         * Declare the variable names available to compiled expressions.
         * <p>
         * The order given is the positional order used by
         * {@link CompiledExpression#evaluate(double...)}. Calling this a second time replaces the
         * previous set rather than adding to it.
         *
         * @param names The variable names.
         * @return This builder.
         */
        @NotNull
        Builder variables(@NotNull String... names);

        /**
         * Register a custom function of fixed arity.
         * <p>
         * The function is treated as deterministic.
         * <p>
         * <b>The argument array passed to the function is pooled and reused.</b> Implementations
         * must not retain a reference to it beyond the call, and must read every value they need
         * before triggering any other expression evaluation.
         *
         * @param name     The function name.
         * @param arity    The exact number of arguments.
         * @param function The function.
         * @return This builder.
         */
        @NotNull
        Builder function(@NotNull String name,
                         int arity,
                         @NotNull ToDoubleFunction<double[]> function);

        /**
         * Register a variadic custom function.
         * <p>
         * The function is treated as deterministic and accepts any number of arguments, including
         * zero.
         * <p>
         * <b>The argument array passed to the function is pooled and reused.</b> Implementations
         * must not retain a reference to it beyond the call, and must read every value they need
         * before triggering any other expression evaluation.
         *
         * @param name     The function name.
         * @param function The function.
         * @return This builder.
         */
        @NotNull
        Builder function(@NotNull String name,
                         @NotNull ToDoubleFunction<double[]> function);

        /**
         * Register a custom function of fixed arity, declaring whether it is deterministic.
         * <p>
         * Pass false if the function's result can differ for identical arguments, for example
         * because it draws on randomness or mutable external state. Any expression calling a
         * non-deterministic function reports {@link CompiledExpression#isDeterministic()} as false.
         * <p>
         * <b>The argument array passed to the function is pooled and reused.</b> Implementations
         * must not retain a reference to it beyond the call, and must read every value they need
         * before triggering any other expression evaluation.
         *
         * @param name          The function name.
         * @param arity         The exact number of arguments.
         * @param function      The function.
         * @param deterministic Whether the function is deterministic.
         * @return This builder.
         */
        @NotNull
        Builder function(@NotNull String name,
                         int arity,
                         @NotNull ToDoubleFunction<double[]> function,
                         boolean deterministic);

        /**
         * Build the environment.
         *
         * @return The environment.
         * @throws IllegalArgumentException If a custom function name collides with a built-in
         *                                  function, a unary operator, a constant, a declared
         *                                  variable, or another custom function.
         */
        @NotNull
        ExpressionEnvironment build();
    }
}
