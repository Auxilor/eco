package com.willfp.eco.util;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.placeholder.AdditionalPlayer;
import com.willfp.eco.core.placeholder.PlaceholderInjectable;
import com.willfp.eco.core.placeholder.context.PlaceholderContext;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utilities / API methods for numbers.
 */
public final class NumberUtils {
    /**
     * Sin lookup table, holding one full period of sine sampled at 65536 points.
     */
    private static final double[] SIN_LOOKUP = new double[65536];

    /**
     * Set of roman numerals to look up, mapping value to numeral.
     */
    private static final ConcurrentSkipListMap<Integer, String> NUMERALS = new ConcurrentSkipListMap<>();

    /**
     * Epsilon, the tolerance used when comparing doubles for equality.
     */
    private static final double EPSILON = 1e-6;

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
     * Get the sine of an angle.
     * <p>
     * This is an approximation read from a lookup table rather than an exact calculation, so it is
     * faster but less precise than {@link Math#sin(double)}.
     *
     * @param a The angle, in radians.
     * @return The approximate sine, between -1 and 1.
     */
    public static double fastSin(final double a) {
        float f = (float) a;
        return SIN_LOOKUP[(int) (f * 10430.378F) & '\uffff'];
    }

    /**
     * Get the cosine of an angle.
     * <p>
     * This is an approximation read from a lookup table rather than an exact calculation, so it is
     * faster but less precise than {@link Math#cos(double)}.
     *
     * @param a The angle, in radians.
     * @return The approximate cosine, between -1 and 1.
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
     * <p>
     * Only numbers from 1 to 4096 inclusive are converted; anything outside that range is returned
     * as its plain decimal string.
     *
     * @param number The number to convert.
     * @return The number, converted to a roman numeral, or its decimal string if out of range.
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
     * @return The number, converted from a roman numeral, or zero if the string is empty or does
     *         not start with a recognised numeral.
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
     * Generate random integer in range, where both bounds are inclusive.
     *
     * @param min Minimum, inclusive.
     * @param max Maximum, inclusive.
     * @return Random integer, or min if min and max are equal.
     */
    public static int randInt(final int min,
                              final int max) {
        if (min == max) {
            return min;
        }

        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    /**
     * Generate random double in range, where the minimum is inclusive and the maximum exclusive.
     * <p>
     * The bounds are ordered internally, so passing them the wrong way round is safe.
     *
     * @param min Minimum, inclusive.
     * @param max Maximum, exclusive.
     * @return Random double, or min if the two bounds differ by less than 1e-6.
     */
    public static double randFloat(final double min,
                                   final double max) {
        if (Math.abs(min - max) < EPSILON) {
            return min;
        }

        double tMin = Math.min(min, max);
        double tMax = Math.max(min, max);

        return ThreadLocalRandom.current().nextDouble(tMin, tMax);
    }

    /**
     * Generate random double with a triangular distribution.
     *
     * @param minimum Minimum, inclusive.
     * @param maximum Maximum, inclusive.
     * @param peak    The most likely value, which should lie between the minimum and the maximum.
     * @return Random double between the minimum and the maximum.
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
     * Get Log base 2 of a number, truncated to an integer.
     *
     * @param a The number.
     * @return The result, rounded towards zero.
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
     * <p>
     * The number is rounded to two decimal places, and whole numbers are rendered without any
     * decimal part, so {@code 12.345} becomes {@code 12.35} and {@code 12.0} becomes {@code 12}.
     *
     * @param toFormat The number to format.
     * @return Formatted.
     */
    @NotNull
    public static String format(final double toFormat) {
        DecimalFormat df = new DecimalFormat("0.00");
        String formatted = df.format(toFormat);

        return formatted.endsWith("00") ? String.valueOf((long) toFormat) : formatted;
    }

    /**
     * Format double to string with comma thousands separators.
     * <p>
     * The number is rounded to two decimal places and a trailing {@code .00} is stripped, so
     * {@code 1234.5} becomes {@code 1,234.50} and {@code 1234.0} becomes {@code 1,234}.
     *
     * @param toFormat The number to format.
     * @return Formatted.
     */
    @NotNull
    public static String formatWithCommas(final double toFormat) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        String formatted = df.format(toFormat);

        return formatted.endsWith(".00") ? formatted.substring(0, formatted.length() - 3) : formatted;
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
     * @param player     The player, or null to evaluate without a player.
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
     * @param player     The player, or null to evaluate without a player.
     * @param context    The injectable context to resolve placeholders against, or null for none.
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
     * @param player            The player, or null to evaluate without a player.
     * @param context           The injectable context to resolve placeholders against, or null for none.
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
     * @return The value of the expression, or null if invalid.
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
