package com.willfp.eco.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonSyntaxException;
import com.willfp.eco.core.Eco;
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utilities / API methods for strings.
 */
public final class StringUtils {
    /**
     * Regexes for gradients.
     */
    private static final List<Pattern> GRADIENT_PATTERNS = new ImmutableList.Builder<Pattern>()
            .add(Pattern.compile("<GRADIENT:([0-9A-Fa-f]{6})>(.*?)</GRADIENT:([0-9A-Fa-f]{6})>", Pattern.CASE_INSENSITIVE))
            .add(Pattern.compile("<GRADIENT:#([0-9A-Fa-f]{6})>(.*?)</GRADIENT:#([0-9A-Fa-f]{6})>", Pattern.CASE_INSENSITIVE))
            .add(Pattern.compile("<G:([0-9A-Fa-f]{6})>(.*?)</G:([0-9A-Fa-f]{6})>", Pattern.CASE_INSENSITIVE))
            .add(Pattern.compile("<G:#([0-9A-Fa-f]{6})>(.*?)</G:#([0-9A-Fa-f]{6})>", Pattern.CASE_INSENSITIVE))
            .add(Pattern.compile("<G#([0-9A-Fa-f]{6})>(.*?)</G#([0-9A-Fa-f]{6})>", Pattern.CASE_INSENSITIVE))
            .add(Pattern.compile("<#:([0-9A-Fa-f]{6})>(.*?)</#:([0-9A-Fa-f]{6})>"))
            .add(Pattern.compile("\\{#:([0-9A-Fa-f]{6})}(.*?)\\{/#:([0-9A-Fa-f]{6})}"))
            .add(Pattern.compile("\\{#([0-9A-Fa-f]{6})>}(.*?)\\{#([0-9A-Fa-f]{6})<}"))
            .build();

    /**
     * Regexes for hex codes.
     */
    private static final List<Pattern> HEX_PATTERNS = new ImmutableList.Builder<Pattern>()
            .add(Pattern.compile("&#" + "([A-Fa-f0-9]{6})" + ""))
            .add(Pattern.compile("\\{#" + "([A-Fa-f0-9]{6})" + "}"))
            .add(Pattern.compile("<#" + "([A-Fa-f0-9]{6})" + ">"))
            .build();

    /**
     * Legacy serializer.
     */
    private static final LegacyComponentSerializer LEGACY_COMPONENT_SERIALIZER = LegacyComponentSerializer.builder()
            .character('\u00a7')
            .useUnusualXRepeatedCharacterHexFormat()
            .hexColors()
            .build();

    /**
     * GSON serializer.
     */
    private static final GsonComponentSerializer GSON_COMPONENT_SERIALIZER = GsonComponentSerializer.builder()
            .emitLegacyHoverEvent()
            .build();

    /**
     * String format cache.
     */
    private static final LoadingCache<String, String> STRING_FORMAT_CACHE = Caffeine.newBuilder()
            .expireAfterAccess(10, TimeUnit.SECONDS)
            .build(StringUtils::processFormatting);

    /**
     * Json -> Component Cache.
     */
    private static final Cache<String, Component> JSON_TO_COMPONENT = Caffeine.newBuilder()
            .expireAfterAccess(10, TimeUnit.SECONDS)
            .build();

    /**
     * Component -> Json Cache.
     */
    private static final Cache<Component, String> COMPONENT_TO_JSON = Caffeine.newBuilder()
            .expireAfterAccess(10, TimeUnit.SECONDS)
            .build();

    /**
     * Legacy -> Component Cache.
     */
    private static final Cache<String, Component> LEGACY_TO_COMPONENT = Caffeine.newBuilder()
            .expireAfterAccess(10, TimeUnit.SECONDS)
            .build();

    /**
     * Component -> Legacy Cache.
     */
    private static final Cache<Component, String> COMPONENT_TO_LEGACY = Caffeine.newBuilder()
            .expireAfterAccess(10, TimeUnit.SECONDS)
            .build();

    /**
     * Empty JSON.
     */
    private static final String EMPTY_JSON = GSON_COMPONENT_SERIALIZER.serialize(Component.empty());

    /**
     * Color map.
     */
    private static final Map<String, ChatColor> COLOR_MAP = new ImmutableMap.Builder<String, ChatColor>()
            .put("&l", ChatColor.BOLD)
            .put("&o", ChatColor.ITALIC)
            .put("&n", ChatColor.UNDERLINE)
            .put("&m", ChatColor.STRIKETHROUGH)
            .put("&k", ChatColor.MAGIC)
            .put("§l", ChatColor.BOLD)
            .put("§o", ChatColor.ITALIC)
            .put("§n", ChatColor.UNDERLINE)
            .put("§m", ChatColor.STRIKETHROUGH)
            .put("§k", ChatColor.MAGIC)
            .build();

    /**
     * Regex map for splitting values.
     */
    private static final LoadingCache<String, Pattern> SPACE_AROUND_CHARACTER = Caffeine.newBuilder()
            .build(
                    character -> Pattern.compile("( " + Pattern.quote(character) + " )")
            );

    /**
     * Format a list of strings.
     * <p>
     * Converts color codes and placeholders.
     *
     * @param list The messages to format.
     * @return The message, formatted.
     */
    @NotNull
    public static List<String> formatList(@NotNull final List<String> list) {
        return formatList(list, (Player) null);
    }

    /**
     * Format a list of strings.
     * <p>
     * Coverts color codes and placeholders for a player.
     *
     * @param list   The messages to format.
     * @param player The player to translate placeholders with respect to.
     * @return The message, format.
     */
    @NotNull
    public static List<String> formatList(@NotNull final List<String> list,
                                          @Nullable final Player player) {
        return formatList(list, player, FormatOption.WITH_PLACEHOLDERS);
    }

    /**
     * Format a list of strings.
     * <p>
     * Converts color codes and placeholders if specified.
     *
     * @param list   The messages to format.
     * @param option The format option.
     * @return The message, formatted.
     */
    @NotNull
    public static List<String> formatList(@NotNull final List<String> list,
                                          @NotNull final FormatOption option) {
        return formatList(list, null, option);
    }

    /**
     * Format a list of strings.
     * <p>
     * Coverts color codes and placeholders for a player if specified.
     *
     * @param list   The messages to format.
     * @param player The player to translate placeholders with respect to.
     * @param option The options.
     * @return The message, format.
     */
    @NotNull
    public static List<String> formatList(@NotNull final List<String> list,
                                          @Nullable final Player player,
                                          @NotNull final FormatOption option) {
        List<String> translated = new ArrayList<>();
        for (String string : list) {
            translated.add(format(string, player, option));
        }

        return translated;
    }

    /**
     * Format a string.
     * <p>
     * Converts color codes and placeholders.
     *
     * @param message The message to translate.
     * @return The message, formatted.
     * @see StringUtils#format(String, Player)
     */
    @NotNull
    public static String format(@NotNull final String message) {
        return format(message, (Player) null);
    }

    /**
     * Format a string.
     * <p>
     * Converts color codes and placeholders for a player.
     *
     * @param message The message to format.
     * @param player  The player to translate placeholders with respect to.
     * @return The message, formatted.
     */
    @NotNull
    public static String format(@NotNull final String message,
                                @Nullable final Player player) {
        return format(message, player, FormatOption.WITH_PLACEHOLDERS);
    }

    /**
     * Format a string.
     * <p>
     * Converts color codes and placeholders if specified.
     *
     * @param message The message to translate.
     * @param option  The format option.
     * @return The message, formatted.
     * @see StringUtils#format(String, Player)
     */
    @NotNull
    public static String format(@NotNull final String message,
                                @NotNull final FormatOption option) {
        return format(message, null, option);
    }

    /**
     * Format a string to a component.
     * <p>
     * Converts color codes and placeholders.
     *
     * @param message The message to translate.
     * @return The message, formatted, as a component.
     * @see StringUtils#format(String, Player)
     */
    @NotNull
    public static Component formatToComponent(@NotNull final String message) {
        return formatToComponent(message, (Player) null);
    }

    /**
     * Format a string.
     * <p>
     * Converts color codes and placeholders for a player.
     *
     * @param message The message to format.
     * @param player  The player to translate placeholders with respect to.
     * @return The message, formatted.
     */
    @NotNull
    public static Component formatToComponent(@NotNull final String message,
                                              @Nullable final Player player) {
        return formatToComponent(message, player, FormatOption.WITH_PLACEHOLDERS);
    }

    /**
     * Format a string to a component.
     * <p>
     * Converts color codes and placeholders if specified.
     *
     * @param message The message to translate.
     * @param option  The format option.
     * @return The message, formatted, as a component.
     * @see StringUtils#format(String, Player)
     */
    @NotNull
    public static Component formatToComponent(@NotNull final String message,
                                              @NotNull final FormatOption option) {
        return formatToComponent(message, null, option);
    }

    /**
     * Format a string to a component.
     * <p>
     * Coverts color codes and placeholders for a player if specified.
     *
     * @param message The message to format.
     * @param player  The player to translate placeholders with respect to.
     * @param option  The format options.
     * @return The message, formatted, as a component.
     */
    @NotNull
    public static Component formatToComponent(@NotNull final String message,
                                              @Nullable final Player player,
                                              @NotNull final FormatOption option) {
        return toComponent(format(message, player, option));
    }

    /**
     * Format a string.
     * <p>
     * Coverts color codes and placeholders for a player if specified.
     *
     * @param message The message to format.
     * @param player  The player to translate placeholders with respect to.
     * @param option  The format options.
     * @return The message, formatted.
     */
    @NotNull
    public static String format(@NotNull final String message,
                                @Nullable final Player player,
                                @NotNull final FormatOption option) {
        String processedMessage = message;
        if (option == FormatOption.WITH_PLACEHOLDERS) {
            processedMessage = PlaceholderManager.translatePlaceholders(processedMessage, player);
        }
        return STRING_FORMAT_CACHE.get(processedMessage);
    }

    private static String processFormatting(@NotNull final String message) {
        String processedMessage = message;
        // Run MiniMessage first so it doesn't complain
        processedMessage = translateMiniMessage(processedMessage);
        processedMessage = ChatColor.translateAlternateColorCodes('&', processedMessage);
        processedMessage = translateGradients(processedMessage);
        processedMessage = translateHexColorCodes(processedMessage);
        return processedMessage;
    }

    private static String translateMiniMessage(@NotNull final String message) {
        return Eco.get().formatMiniMessage(message);
    }

    private static String translateHexColorCodes(@NotNull final String message) {
        String processedMessage = message;
        for (Pattern pattern : HEX_PATTERNS) {
            processedMessage = translateHexColorCodes(processedMessage, pattern);
        }
        return processedMessage;
    }

    private static String translateHexColorCodes(@NotNull final String message,
                                                 @NotNull final Pattern pattern) {
        Matcher matcher = pattern.matcher(message);

        StringBuilder builder = new StringBuilder(message.length() + 4 * 8);
        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(builder, ChatColor.COLOR_CHAR + "x"
                    + ChatColor.COLOR_CHAR + group.charAt(0) + ChatColor.COLOR_CHAR + group.charAt(1)
                    + ChatColor.COLOR_CHAR + group.charAt(2) + ChatColor.COLOR_CHAR + group.charAt(3)
                    + ChatColor.COLOR_CHAR + group.charAt(4) + ChatColor.COLOR_CHAR + group.charAt(5));
        }

        return matcher.appendTail(builder).toString();
    }

    private static String processGradients(@NotNull final String string,
                                           @NotNull final Color start,
                                           @NotNull final Color end) {
        String processedString = string;
        List<ChatColor> modifiers = new ArrayList<>();
        for (Map.Entry<String, ChatColor> entry : COLOR_MAP.entrySet()) {
            if (processedString.contains(entry.getKey())) {
                modifiers.add(entry.getValue());
            }
            processedString = processedString.replace(entry.getKey(), "");
        }

        StringBuilder stringBuilder = new StringBuilder();
        ChatColor[] colors = getGradientColors(start, end, processedString.length());
        String[] characters = processedString.split("");
        for (int i = 0; i < processedString.length(); i++) {
            stringBuilder.append(colors[i]);
            modifiers.forEach(stringBuilder::append);
            stringBuilder.append(characters[i]);
        }
        return stringBuilder.toString();
    }

    private static ChatColor[] getGradientColors(@NotNull final Color start,
                                                 @NotNull final Color end,
                                                 final int step) {
        ChatColor[] colors = new ChatColor[step];
        if (step <= 1) {
            return colors;
        }
        int stepR = Math.abs(start.getRed() - end.getRed()) / (step - 1);
        int stepG = Math.abs(start.getGreen() - end.getGreen()) / (step - 1);
        int stepB = Math.abs(start.getBlue() - end.getBlue()) / (step - 1);
        int[] direction = new int[]{
                start.getRed() < end.getRed() ? +1 : -1,
                start.getGreen() < end.getGreen() ? +1 : -1,
                start.getBlue() < end.getBlue() ? +1 : -1
        };

        for (int i = 0; i < step; i++) {
            Color color = new Color(start.getRed() + ((stepR * i) * direction[0]), start.getGreen() + ((stepG * i) * direction[1]), start.getBlue() + ((stepB * i) * direction[2]));
            colors[i] = ChatColor.of(color);
        }
        return colors;
    }

    private static String translateGradients(@NotNull final String string) {
        String processedString = string;
        for (Pattern pattern : GRADIENT_PATTERNS) {
            Matcher matcher = pattern.matcher(string);
            while (matcher.find()) {
                String start = matcher.group(1);
                String end = matcher.group(3);
                String content = matcher.group(2);
                processedString = processedString.replace(matcher.group(), processGradients(content, new Color(Integer.parseInt(start, 16)), new Color(Integer.parseInt(end, 16))));
            }
        }
        return processedString;
    }

    /**
     * Internal implementation of {@link String#valueOf}.
     * Formats collections and doubles better.
     *
     * @param object The object to convert to string.
     * @return The object stringified.
     * @deprecated Poorly named method. Use {@link StringUtils#toNiceString(Object)} instead.
     */
    @NotNull
    @Deprecated(since = "6.26.0", forRemoval = true)
    public static String internalToString(@Nullable final Object object) {
        return toNiceString(object);
    }

    /**
     * Internal implementation of {@link String#valueOf}.
     * Formats collections and doubles better.
     *
     * @param object The object to convert to string.
     * @return The object stringified.
     */
    @NotNull
    public static String toNiceString(@Nullable final Object object) {
        if (object == null) {
            return "null";
        }

        if (object instanceof Integer) {
            return ((Integer) object).toString();
        } else if (object instanceof String) {
            return (String) object;
        } else if (object instanceof Double) {
            return NumberUtils.format((Double) object);
        } else if (object instanceof Collection<?> c) {
            return c.stream().map(StringUtils::toNiceString).collect(Collectors.joining(", "));
        } else {
            return String.valueOf(object);
        }
    }

    /**
     * Remove a string of characters from the start of a string.
     *
     * @param string The string to remove the prefix from.
     * @param prefix The substring to remove.
     * @return The string with the prefix removed.
     */
    @NotNull
    public static String removePrefix(@NotNull final String string,
                                      @NotNull final String prefix) {
        if (string.startsWith(prefix)) {
            return string.substring(prefix.length());
        }
        return string;
    }

    /**
     * Convert legacy string to JSON.
     *
     * @param legacy The legacy string.
     * @return The JSON String.
     */
    @NotNull
    public static String legacyToJson(@Nullable final String legacy) {
        return componentToJson(toComponent(legacy));
    }

    /**
     * Convert JSON string to legacy.
     *
     * @param json The JSON string.
     * @return The legacy string.
     */
    @NotNull
    public static String jsonToLegacy(@Nullable final String json) {
        return toLegacy(jsonToComponent(json));
    }

    /**
     * Convert Component to JSON String.
     *
     * @param component The Component.
     * @return The JSON string.
     */
    @NotNull
    public static String componentToJson(@Nullable final Component component) {
        if (component == null) {
            return EMPTY_JSON;
        }

        return COMPONENT_TO_JSON.get(component, it -> {
            try {
                return GSON_COMPONENT_SERIALIZER.serialize(
                        Component.empty().decoration(TextDecoration.ITALIC, false).append(
                                it
                        )
                );
            } catch (JsonSyntaxException e) {
                return GSON_COMPONENT_SERIALIZER.serialize(Component.empty());
            }
        });
    }

    /**
     * Convert JSON String to Component.
     *
     * @param json The JSON String.
     * @return The component.
     */
    @NotNull
    public static Component jsonToComponent(@Nullable final String json) {
        if (json == null || json.isEmpty()) {
            return Component.empty();
        }

        return JSON_TO_COMPONENT.get(json, it -> {
            try {
                return GSON_COMPONENT_SERIALIZER.deserialize(it);
            } catch (JsonSyntaxException e) {
                return Component.empty();
            }
        });
    }

    /**
     * Convert legacy (bukkit) text to Component.
     *
     * @param legacy The legacy text.
     * @return The component.
     */
    @NotNull
    public static Component toComponent(@Nullable final String legacy) {
        return LEGACY_TO_COMPONENT.get(legacy == null ? "" : legacy, LEGACY_COMPONENT_SERIALIZER::deserialize);
    }

    /**
     * Convert Component to legacy (bukkit) text.
     *
     * @param component The component.
     * @return The legacy text.
     */
    @NotNull
    public static String toLegacy(@NotNull final Component component) {
        return COMPONENT_TO_LEGACY.get(component, LEGACY_COMPONENT_SERIALIZER::serialize);
    }

    /**
     * Parse string into tokens.
     * <p>
     * Handles quoted strings for names.
     *
     * @param lookup The lookup string.
     * @return An array of tokens to be processed.
     * @author Shawn (https://stackoverflow.com/questions/70606170/split-a-list-on-spaces-and-group-quoted-characters/70606653#70606653)
     */
    @NotNull
    public static String[] parseTokens(@NotNull final String lookup) {
        char[] chars = lookup.toCharArray();
        List<String> tokens = new ArrayList<>();
        StringBuilder tokenBuilder = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == ' ') {
                /*
                Take the current value of the argument builder, append it to the
                list of found tokens, and then clear it for the next argument.
                 */
                tokens.add(tokenBuilder.toString());
                tokenBuilder.setLength(0);
            } else if (chars[i] == '"') {
                /*
                Work until the next unescaped quote to handle quotes with
                spaces in them - assumes the input string is well-formatted
                 */
                for (i++; chars[i] != '"'; i++) {
                    /*
                    If the found quote is escaped, ignore it in the parsing
                     */
                    if (chars[i] == '\\') {
                        i++;
                    }
                    tokenBuilder.append(chars[i]);
                }
            } else {
                /*
                If it's a regular character, just append it to the current argument.
                 */
                tokenBuilder.append(chars[i]);
            }
        }
        tokens.add(tokenBuilder.toString()); // Adds the last argument to the tokens.
        return tokens.toArray(new String[0]);
    }

    /**
     * Split input string around separator surrounded by spaces.
     * <p>
     * e.g. {@code splitAround("hello ? how are you", "?")} will split, but
     * {@code splitAround("hello? how are you", "?")} will not.
     *
     * @param input     Input string.
     * @param separator Separator.
     * @return The split string.
     */
    @NotNull
    public static String[] splitAround(@NotNull final String input,
                                       @NotNull final String separator) {
        return SPACE_AROUND_CHARACTER.get(separator).split(input);
    }

    /**
     * Create progress bar.
     *
     * @param character        The bar character.
     * @param bars             The number of bars.
     * @param progress         The bar progress, between 0 and 1.
     * @param completeFormat   The color of a complete bar section.
     * @param inProgressFormat The color of an in-progress bar section.
     * @param incompleteFormat The color of an incomplete bar section.
     * @return The progress bar.
     */
    @NotNull
    public static String createProgressBar(final char character,
                                           final int bars,
                                           final double progress,
                                           @NotNull final String completeFormat,
                                           @NotNull final String inProgressFormat,
                                           @NotNull final String incompleteFormat) {
        Validate.isTrue(progress >= 0 && progress <= 1, "Progress must be between 0 and 1!");
        Validate.isTrue(bars > 1, "Must have at least 2 bars!");

        String completeColor = format(completeFormat);
        String inProgressColor = format(inProgressFormat);
        String incompleteColor = format(incompleteFormat);

        StringBuilder builder = new StringBuilder();

        // Full bar special case.
        if (progress == 1) {
            builder.append(completeColor);
            builder.append(String.valueOf(character).repeat(bars));
            return builder.toString();
        }

        int completeBars = (int) Math.floor(progress * bars);
        int incompleteBars = bars - completeBars - 1;

        if (completeBars > 0) {
            builder.append(completeColor)
                    .append(String.valueOf(character).repeat(completeBars));
        }

        builder.append(inProgressColor)
                .append(character);

        if (incompleteBars > 0) {
            builder.append(incompleteColor)
                    .append(String.valueOf(character).repeat(incompleteBars));
        }

        return builder.toString();
    }

    /**
     * Options for formatting.
     */
    public enum FormatOption {
        /**
         * Completely formatted.
         */
        WITH_PLACEHOLDERS,

        /**
         * Completely formatted without placeholders.
         */
        WITHOUT_PLACEHOLDERS
    }

    private StringUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
