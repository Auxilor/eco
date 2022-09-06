package com.willfp.eco.util;

import com.willfp.eco.core.placeholder.AdditionalPlayer;
import com.willfp.eco.core.placeholder.InjectablePlaceholder;
import com.willfp.eco.core.placeholder.PlaceholderInjectable;
import com.willfp.eco.core.placeholder.StaticPlaceholder;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
     * Crunch handler.
     */
    private static CrunchHandler crunch = null;

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
     * If value is above maximum, set it to maximum.
     *
     * @param toChange The value to test.
     * @param limit    The maximum.
     * @return The new value.
     * @deprecated Pointless method.
     */
    @Deprecated(since = "6.19.0")
    public static int equalIfOver(final int toChange,
                                  final int limit) {
        return Math.min(toChange, limit);
    }

    /**
     * If value is above maximum, set it to maximum.
     *
     * @param toChange The value to test.
     * @param limit    The maximum.
     * @return The new value.
     * @deprecated Pointless method.
     */
    @Deprecated(since = "6.19.0", forRemoval = true)
    public static double equalIfOver(final double toChange,
                                     final double limit) {
        return Math.min(toChange, limit);
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
        return evaluateExpression(expression, null);
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
        return evaluateExpression(expression, player, new PlaceholderInjectable() {
            @Override
            public void clearInjectedPlaceholders() {
                // Nothing.
            }

            @Override
            public @NotNull
            List<InjectablePlaceholder> getPlaceholderInjections() {
                return Collections.emptyList();
            }
        });
    }

    /**
     * Evaluate an expression with respect to a player (for placeholders).
     *
     * @param expression The expression.
     * @param player     The player.
     * @param statics    The static placeholders.
     * @return The value of the expression, or zero if invalid.
     * @deprecated Use new statics system.
     */
    @Deprecated(since = "6.35.0", forRemoval = true)
    public static double evaluateExpression(@NotNull final String expression,
                                            @Nullable final Player player,
                                            @NotNull final Iterable<StaticPlaceholder> statics) {
        return evaluateExpression(expression, player, new PlaceholderInjectable() {
            @Override
            public void clearInjectedPlaceholders() {
                // Do nothing.
            }

            @Override
            public @NotNull List<InjectablePlaceholder> getPlaceholderInjections() {
                List<InjectablePlaceholder> injections = new ArrayList<>();
                for (StaticPlaceholder placeholder : statics) {
                    injections.add(placeholder);
                }
                return injections;
            }
        });
    }

    /**
     * Evaluate an expression with respect to a player (for placeholders).
     *
     * @param expression The expression.
     * @param player     The player.
     * @param context    The injectable placeholders.
     * @return The value of the expression, or zero if invalid.
     */
    public static double evaluateExpression(@NotNull final String expression,
                                            @Nullable final Player player,
                                            @NotNull final PlaceholderInjectable context) {
        return evaluateExpression(expression, player, context, new ArrayList<>());
    }

    /**
     * Evaluate an expression with respect to a player (for placeholders).
     *
     * @param expression        The expression.
     * @param player            The player.
     * @param context           The injectable placeholders.
     * @param additionalPlayers Additional players to parse placeholders for.
     * @return The value of the expression, or zero if invalid.
     */
    public static double evaluateExpression(@NotNull final String expression,
                                            @Nullable final Player player,
                                            @NotNull final PlaceholderInjectable context,
                                            @NotNull final Collection<AdditionalPlayer> additionalPlayers) {
        return crunch.evaluate(expression, player, context, additionalPlayers);
    }

    /**
     * Init crunch handler.
     *
     * @param handler The handler.
     */
    @ApiStatus.Internal
    public static void initCrunch(@NotNull final CrunchHandler handler) {
        Validate.isTrue(crunch == null, "Already initialized!");
        crunch = handler;
    }

    /**
     * Bridge component for crunch.
     */
    @ApiStatus.Internal
    public interface CrunchHandler {
        /**
         * Evaluate an expression.
         *
         * @param expression        The expression.
         * @param player            The player.
         * @param injectable        The injectable placeholders.
         * @param additionalPlayers The additional players.
         * @return The value of the expression, or zero if invalid.
         */
        double evaluate(@NotNull String expression,
                        @Nullable Player player,
                        @NotNull PlaceholderInjectable injectable,
                        @NotNull Collection<AdditionalPlayer> additionalPlayers);
    }

    private NumberUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
