package com.willfp.eco.core.math;

import org.jetbrains.annotations.NotNull;

/**
 * Thrown when an expression fails to compile.
 *
 * @see ExpressionEnvironment#compileOrThrow(String)
 */
public class ExpressionCompilationException extends RuntimeException {
    /**
     * The expression that failed to compile.
     */
    private final String expression;

    /**
     * The character offset at which compilation failed.
     */
    private final int position;

    /**
     * Create a new compilation exception.
     *
     * @param message    The message.
     * @param expression The expression that failed to compile.
     * @param position   The character offset at which compilation failed.
     */
    public ExpressionCompilationException(@NotNull final String message,
                                          @NotNull final String expression,
                                          final int position) {
        super(message);
        this.expression = expression;
        this.position = position;
    }

    /**
     * Get the expression that failed to compile.
     *
     * @return The expression.
     */
    @NotNull
    public String getExpression() {
        return this.expression;
    }

    /**
     * Get the character offset within the expression at which compilation failed.
     *
     * @return The position, or -1 if unknown.
     */
    public int getPosition() {
        return this.position;
    }
}
