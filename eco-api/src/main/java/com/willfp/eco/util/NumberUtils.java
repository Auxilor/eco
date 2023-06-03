package com.willfp.eco.util;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.placeholder.AdditionalPlayer;
import com.willfp.eco.core.placeholder.PlaceholderInjectable;
import com.willfp.eco.core.placeholder.context.PlaceholderContext;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Utilities / API methods for numbers.
 */
public final class NumberUtils {
    /**
     * Sin lookup table.
     */
    private static final double[] SIN_LOOKUP = new double[65536];

    /**
     * Set of roman numerals to look up.
     */
    private static final TreeMap<Integer, String> NUMERALS = new TreeMap<>();

    static {
        NUMERALS.put(1000, "M");
        NUMERALS.put(900, "CM");
        NUMERALS.put(500, "D");
        NUMERALS.put(400, "CD");
        NUMERALS.put(100, "C");
        NUMERALS.put(90, "XC");
        NUMERALS.put(50, "L");
        NUMERALS.put(40, "XL");
        NUMERALS.put(10, "X");
        NUMERALS.put(9, "IX");
        NUMERALS.put(5, "V");
        NUMERALS.put(4, "IV");
        NUMERALS.put(1, "I");

        for (int i = 0; i < 65536; ++i) {
            SIN_LOOKUP[i] = Math.sin((double) i * 3.141592653589793D * 2.0D / 65536.0D);
        }
    }

    /**
     * Get the sin of a number.
     *
     * @param a The number.
     * @return The sin.
     */
    public static double fastSin(final double a) {
        float f = (float) a;
        return SIN_LOOKUP[(int) (f * 10430.378F) & '\uffff'];
    }

    /**
     * Get the cosine of a number.
     *
     * @param a The number.
     * @return The cosine.
     */
    public static double fastCos(final double a) {
        float f = (float) a;
        return SIN_LOOKUP[(int) (f * 10430.378F + 16384.0F) & '\uffff'];
    }

    /**
     * Bias the input value according to a curve.
     *
     * @param input The input value.
     * @param bias  The bias between -1 and 1, where higher values bias input values to lower output values.
     * @return The biased output.
     */
    public static double bias(final double input,
                              final double bias) {
        double k = Math.pow(1 - bias, 3);

        return (input * k) / (input * k - input + 1);
    }

    /**
     * Get Roman Numeral from number.
     *
     * @param number The number to convert.
     * @return The number, converted to a roman numeral.
     */
    @NotNull
    public static String toNumeral(final int number) {
        if (number >= 1 && number <= 4096) {
            int l = NUMERALS.floorKey(number);
            if (number == l) {
                return NUMERALS.get(number);
            }
            return NUMERALS.get(l) + toNumeral(number - l);
        } else {
            return String.valueOf(number);
        }
    }

    /**
     * Get number from roman numeral.
     *
     * @param numeral The numeral to convert.
     * @return The number, converted from a roman numeral.
     */
    public static int fromNumeral(@NotNull final String numeral) {
        if (numeral.isEmpty()) {
            return 0;
        }
        for (Map.Entry<Integer, String> entry : NUMERALS.descendingMap().entrySet()) {
            if (numeral.startsWith(entry.getValue())) {
                return entry.getKey() + fromNumeral(numeral.substring(entry.getValue().length()));
            }
        }
        return 0;
    }

    /**
     * Generate random integer in range.
     *
     * @param min Minimum.
     * @param max Maximum.
     * @return Random integer.
     */
    public static int randInt(final int min,
                              final int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    /**
     * Generate random double in range.
     *
     * @param min Minimum.
     * @param max Maximum.
     * @return Random double.
     */
    public static double randFloat(final double min,
                                   final double max) {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    /**
     * Generate random double with a triangular distribution.
     *
     * @param minimum Minimum.
     * @param maximum Maximum.
     * @param peak    Peak.
     * @return Random double.
     */
    public static double triangularDistribution(final double minimum,
                                                final double maximum,
                                                final double peak) {
        double f = (peak - minimum) / (maximum - minimum);
        double rand = Math.random();
        if (rand < f) {
            return minimum + Math.sqrt(rand * (maximum - minimum) * (peak - minimum));
        } else {
            return maximum - Math.sqrt((1 - rand) * (maximum - minimum) * (maximum - peak));
        }
    }

    /**
     * Get Log base 2 of a number.
     *
     * @param a The number.
     * @return The result.
     */
    public static int log2(final int a) {
        return (int) logBase(a, 2);
    }

    /**
     * Log with a base.
     *
     * @param a    The number.
     * @param base The base.
     * @return The logarithm.
     */
    public static double logBase(final double a,
                                 final double base) {
        return Math.log(a) / Math.log(base);
    }

    /**
     * Format double to string.
     *
     * @param toFormat The number to format.
     * @return Formatted.
     */
    @NotNull
    public static String format(final double toFormat) {
        DecimalFormat df = new DecimalFormat("0.00");
        String formatted = df.format(toFormat);

        return formatted.endsWith("00") ? String.valueOf((int) toFormat) : formatted;
    }

    /**
     * Evaluate an expression.
     *
     * @param expression The expression.
     * @return The value of the expression, or zero if invalid.
     */
    public static double evaluateExpression(@NotNull final String expression) {
        return evaluateExpression(expression, PlaceholderContext.EMPTY);
    }

    /**
     * Evaluate an expression with respect to a player (for placeholders).
     *
     * @param expression The expression.
     * @param player     The player.
     * @return The value of the expression, or zero if invalid.
     */
    public static double evaluateExpression(@NotNull final String expression,
                                            @Nullable final Player player) {
        return evaluateExpression(expression, player, null);
    }

    /**
     * Evaluate an expression with respect to a player (for placeholders).
     *
     * @param expression The expression.
     * @param player     The player.
     * @param context    The injectableContext placeholders.
     * @return The value of the expression, or zero if invalid.
     */
    public static double evaluateExpression(@NotNull final String expression,
                                            @Nullable final Player player,
                                            @Nullable final PlaceholderInjectable context) {
        return evaluateExpression(expression, player, context, new ArrayList<>());
    }

    /**
     * Evaluate an expression with respect to a player (for placeholders).
     *
     * @param expression        The expression.
     * @param player            The player.
     * @param context           The injectableContext placeholders.
     * @param additionalPlayers Additional players to parse placeholders for.
     * @return The value of the expression, or zero if invalid.
     */
    public static double evaluateExpression(@NotNull final String expression,
                                            @Nullable final Player player,
                                            @Nullable final PlaceholderInjectable context,
                                            @NotNull final Collection<AdditionalPlayer> additionalPlayers) {
        return evaluateExpression(
                expression,
                new PlaceholderContext(
                        player,
                        null,
                        context,
                        additionalPlayers
                )
        );
    }

    /**
     * Evaluate an expression in a context.
     *
     * @param expression The expression.
     * @param context    The context.
     * @return The value of the expression, or zero if invalid.
     * @deprecated Use {@link #evaluateExpression(String, PlaceholderContext)} instead.
     */
    @Deprecated(since = "6.56.0", forRemoval = true)
    @SuppressWarnings({"removal", "DeprecatedIsStillUsed"})
    public static double evaluateExpression(@NotNull final String expression,
                                            @NotNull final com.willfp.eco.core.math.MathContext context) {
        return evaluateExpression(expression, context.toPlaceholderContext());
    }

    /**
     * Evaluate an expression in a context.
     *
     * @param expression The expression.
     * @param context    The context.
     * @return The value of the expression, or zero if invalid.
     */
    public static double evaluateExpression(@NotNull final String expression,
                                            @NotNull final PlaceholderContext context) {
        return Objects.requireNonNullElse(
                evaluateExpressionOrNull(expression, context),
                0.0
        );
    }

    /**
     * Evaluate an expression in a context.
     *
     * @param expression The expression.
     * @param context    The context.
     * @return The value of the expression, or zero if invalid.
     */
    @Nullable
    public static Double evaluateExpressionOrNull(@NotNull final String expression,
                                                  @NotNull final PlaceholderContext context) {
        return Eco.get().evaluate(expression, context);
    }

    private NumberUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
