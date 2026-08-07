package com.willfp.eco.util;

import com.willfp.eco.core.cache.EcoCache;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonSyntaxException;
import com.willfp.eco.core.Eco;
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager;
import com.willfp.eco.core.placeholder.context.PlaceholderContext;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utilities / API methods for strings.
 * <p>
 * The formatting methods on this class all produce legacy (section sign) coloured text. They apply
 * the following, in order:
 * <ol>
 *     <li>Placeholders of the form <code>%placeholder%</code>, only when a
 *     {@link PlaceholderContext} is given or the {@link FormatOption#WITH_PLACEHOLDERS} option is
 *     used. Placeholders are resolved through {@link PlaceholderManager}, so integrations such as
 *     PlaceholderAPI are honoured.</li>
 *     <li>MiniMessage tags.</li>
 *     <li>Legacy colour and formatting codes written with an ampersand, such as
 *     <code>&amp;a</code> or <code>&amp;l</code>.</li>
 *     <li>Gradients, written as
 *     <code>&lt;GRADIENT:RRGGBB&gt;text&lt;/GRADIENT:RRGGBB&gt;</code>. The <code>GRADIENT</code>
 *     keyword may be abbreviated to <code>G</code> and the hex may be prefixed with
 *     <code>#</code>, and the equivalent forms
 *     <code>&lt;G#RRGGBB&gt;text&lt;/G#RRGGBB&gt;</code>,
 *     <code>&lt;#:RRGGBB&gt;text&lt;/#:RRGGBB&gt;</code>,
 *     <code>{#:RRGGBB}text{/#:RRGGBB}</code> and
 *     <code>{#RRGGBB&gt;}text{#RRGGBB&lt;}</code> are also accepted. Formatting codes inside a
 *     gradient are stripped out and reapplied to every character of it.</li>
 *     <li>Single hex colours, written as <code>&amp;#RRGGBB</code>,
 *     <code>{#RRGGBB}</code> or <code>&lt;#RRGGBB&gt;</code>.</li>
 * </ol>
 * Formatted strings are cached for ten seconds, so repeatedly formatting the same string is cheap.
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
            .add(Pattern.compile("&#" + "([A-Fa-f0-9]{6})"))
            .add(Pattern.compile("\\{#" + "([A-Fa-f0-9]{6})" + "}"))
            .add(Pattern.compile("<#" + "([A-Fa-f0-9]{6})" + ">"))
            .build();

    /**
     * Legacy serializer.
     */
    private static final LegacyComponentSerializer LEGACY_COMPONENT_SERIALIZER = LegacyComponentSerializer.builder()
            .character('§')
            .useUnusualXRepeatedCharacterHexFormat()
            .hexColors()
            .build();

    /**
     * GSON serializer.
     */
    private static final GsonComponentSerializer GSON_COMPONENT_SERIALIZER = GsonComponentSerializer.builder()
            .build();

    /**
     * String format cache.
     */
    private static final EcoCache<String, String> STRING_FORMAT_CACHE = EcoCache.<String, String>builder()
            .expireAfterAccess(Duration.ofSeconds(10))
            .build(StringUtils::processFormatting);

    /**
     * Json -> Component Cache.
     */
    private static final EcoCache<String, Component> JSON_TO_COMPONENT = EcoCache.<String, Component>builder()
            .expireAfterAccess(Duration.ofSeconds(10))
            .build();

    /**
     * Component -> Json Cache.
     */
    private static final EcoCache<Component, String> COMPONENT_TO_JSON = EcoCache.<Component, String>builder()
            .expireAfterAccess(Duration.ofSeconds(10))
            .build();

    /**
     * Legacy -> Component Cache.
     */
    private static final EcoCache<String, Component> LEGACY_TO_COMPONENT = EcoCache.<String, Component>builder()
            .expireAfterAccess(Duration.ofSeconds(10))
            .build();

    /**
     * Component -> Legacy Cache.
     */
    private static final EcoCache<Component, String> COMPONENT_TO_LEGACY = EcoCache.<Component, String>builder()
            .expireAfterAccess(Duration.ofSeconds(10))
            .build();

    /**
     * Empty JSON.
     */
    private static final String EMPTY_JSON = GSON_COMPONENT_SERIALIZER.serialize(Component.empty());

    /**
     * Color map, mapping decoration codes to their {@link ChatColor} equivalents so that
     * decorations used inside a gradient can be stripped out and reapplied per character.
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
     * Regex map for splitting values, caching a pattern that matches the literal separator
     * surrounded by single spaces.
     */
    private static final EcoCache<String, Pattern> SPACE_AROUND_CHARACTER = EcoCache.<String, Pattern>builder()
            .build(character -> Pattern.compile("( " + Pattern.quote(character) + " )"));

    /**
     * Format a list of strings.
     * <p>
     * Converts color codes and placeholders.
     *
     * @param list The messages to format.
     * @return A new list of the messages, formatted.
     */
    @NotNull
    public static List<String> formatList(@NotNull final List<String> list) {
        return formatList(list, (Player) null);
    }

    /**
     * Format a list of strings.
     * <p>
     * Converts color codes and placeholders for a player.
     *
     * @param list   The messages to format.
     * @param player The player to translate placeholders with respect to, or null for none.
     * @return A new list of the messages, formatted.
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
     * @return A new list of the messages, formatted.
     */
    @NotNull
    public static List<String> formatList(@NotNull final List<String> list,
                                          @NotNull final FormatOption option) {
        return formatList(list, null, option);
    }

    /**
     * Format a list of strings.
     * <p>
     * Converts color codes, and placeholders if the option asks for them.
     *
     * @param list   The messages to format.
     * @param player The player to translate placeholders with respect to, or null for none.
     * @param option The options.
     * @return A new list of the messages, formatted.
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
     * Format a list of strings.
     * <p>
     * Converts color codes and placeholders.
     *
     * @param list    The messages to format.
     * @param context The context to translate placeholders with respect to.
     * @return A new list of the messages, formatted.
     */
    @NotNull
    public static List<String> formatList(@NotNull final List<String> list,
                                          @NotNull PlaceholderContext context) {
        List<String> translated = new ArrayList<>();
        for (String string : list) {
            translated.add(format(string, context));
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
     * @param player  The player to translate placeholders with respect to, or null for none.
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
     * Format a string to a component.
     * <p>
     * Converts color codes and placeholders for a player.
     *
     * @param message The message to format.
     * @param player  The player to translate placeholders with respect to, or null for none.
     * @return The message, formatted, as a component.
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
     * Converts color codes, and placeholders if the option asks for them.
     *
     * @param message The message to format.
     * @param player  The player to translate placeholders with respect to, or null for none.
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
     * Converts color codes, and placeholders if the option asks for them. With
     * {@link FormatOption#WITHOUT_PLACEHOLDERS} the player is ignored entirely.
     *
     * @param message The message to format.
     * @param player  The player to translate placeholders with respect to, or null for none.
     * @param option  The format options.
     * @return The message, formatted.
     */
    @NotNull
    public static String format(@NotNull final String message,
                                @Nullable final Player player,
                                @NotNull final FormatOption option) {
        if (option == FormatOption.WITH_PLACEHOLDERS) {
            return format(
                    message,
                    new PlaceholderContext(player)
            );
        }

        return STRING_FORMAT_CACHE.get(message);
    }

    /**
     * Format a string to a component.
     * <p>
     * Converts color codes and placeholders.
     *
     * @param message The message to translate.
     * @param context The placeholder context.
     * @return The message, formatted, as a component.
     * @see StringUtils#format(String, Player)
     */
    @NotNull
    public static Component formatToComponent(@NotNull final String message,
                                              @NotNull final PlaceholderContext context) {
        return toComponent(format(message, context));
    }

    /**
     * Format a string.
     * <p>
     * Converts color codes and placeholders.
     *
     * @param message The message to format.
     * @param context The context to translate placeholders with respect to.
     * @return The message, formatted.
     */
    @NotNull
    public static String format(@NotNull final String message,
                                @NotNull final PlaceholderContext context) {
        String processedMessage = message;
        processedMessage = PlaceholderManager.translatePlaceholders(
                processedMessage,
                context
        );
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
        if (step <= 0) {
            return colors;
        }
        if (step == 1) {
            colors[0] = ChatColor.of(start);
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
     * <p>
     * Doubles are rendered with {@link NumberUtils#format(double)}, and collections are rendered
     * by converting each element with this method and joining them with {@code ", "}. Null becomes
     * the literal string {@code "null"}; everything else falls back to
     * {@link String#valueOf(Object)}.
     *
     * @param object The object to convert to string, may be null.
     * @return The object stringified.
     */
    @NotNull
    public static String toNiceString(@Nullable final Object object) {
        return switch (object) {
            case null -> "null";
            case Integer i -> i.toString();
            case String s -> s;
            case Double v -> NumberUtils.format(v);
            case Collection<?> c -> c.stream().map(StringUtils::toNiceString).collect(Collectors.joining(", "));
            default -> String.valueOf(object);
        };

    }

    /**
     * Remove a string of characters from the start of a string.
     *
     * @param string The string to remove the prefix from.
     * @param prefix The substring to remove.
     * @return The string with the prefix removed, or the string unchanged if it did not start
     *         with the prefix.
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
     * @param legacy The legacy string, may be null.
     * @return The JSON String, or the JSON for an empty component if the input is null.
     */
    @NotNull
    public static String legacyToJson(@Nullable final String legacy) {
        return componentToJson(toComponent(legacy));
    }

    /**
     * Convert JSON string to legacy.
     *
     * @param json The JSON string, may be null.
     * @return The legacy string, or an empty string if the input is null, empty, or invalid JSON.
     */
    @NotNull
    public static String jsonToLegacy(@Nullable final String json) {
        return toLegacy(jsonToComponent(json));
    }

    /**
     * Convert Component to JSON String.
     * <p>
     * The component is wrapped in an empty parent with italics explicitly disabled, so that item
     * names and lore do not pick up the client's default italic styling.
     *
     * @param component The Component, may be null.
     * @return The JSON string, or the JSON for an empty component if the input is null or cannot
     *         be serialized.
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
     * @param json The JSON String, may be null.
     * @return The component, or an empty component if the input is null, empty, or invalid JSON.
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
     * <p>
     * Section sign colour codes are read, including hex colours written in the
     * section-x-repeated form.
     *
     * @param legacy The legacy text, may be null.
     * @return The component, or an empty component if the input is null.
     */
    @NotNull
    public static Component toComponent(@Nullable final String legacy) {
        return LEGACY_TO_COMPONENT.get(legacy == null ? "" : legacy, LEGACY_COMPONENT_SERIALIZER::deserialize);
    }

    /**
     * Convert Component to legacy (bukkit) text.
     * <p>
     * Colours are written as section sign codes, with hex colours written in the
     * section-x-repeated form.
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
     * Tokens are separated by spaces, and a double-quoted run is kept as a single token even if it
     * contains spaces. A quote may be escaped with a backslash. The input is assumed to be
     * well-formed; an unterminated quote will cause an exception.
     *
     * @param lookup The lookup string.
     * @return An array of tokens to be processed.
     * @author Shawn (<a href="https://stackoverflow.com/questions/70606170/split-a-list-on-spaces-and-group-quoted-characters/70606653#70606653">...</a>)
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
     * <p>
     * The separator is matched literally, and the surrounding spaces are consumed along with it.
     *
     * @param input     Input string.
     * @param separator Separator.
     * @return The parts of the input either side of each separator.
     */
    @NotNull
    public static String[] splitAround(@NotNull final String input,
                                       @NotNull final String separator) {
        return SPACE_AROUND_CHARACTER.get(separator).split(input);
    }

    /**
     * Create progress bar.
     * <p>
     * The bar is always exactly the given number of characters long. Unless the bar is completely
     * full, exactly one character is drawn in the in-progress format, sitting between the complete
     * and incomplete sections. The three format strings are themselves run through
     * {@link #format(String)}, so they may contain colour codes in any supported form.
     *
     * @param character        The bar character.
     * @param bars             The number of bars, which must be at least 2.
     * @param progress         The bar progress, between 0 and 1 inclusive.
     * @param completeFormat   The color of a complete bar section.
     * @param inProgressFormat The color of an in-progress bar section.
     * @param incompleteFormat The color of an incomplete bar section.
     * @return The progress bar.
     * @throws IllegalArgumentException If the progress is outside 0 to 1, or there are fewer than
     *                                  2 bars.
     */
    @NotNull
    public static String createProgressBar(final char character,
                                           final int bars,
                                           final double progress,
                                           @NotNull final String completeFormat,
                                           @NotNull final String inProgressFormat,
                                           @NotNull final String incompleteFormat) {
        Preconditions.checkArgument(progress >= 0 && progress <= 1, "Progress must be between 0 and 1!");
        Preconditions.checkArgument(bars > 1, "Must have at least 2 bars!");

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
     * Fast implementation of {@link String#replace(CharSequence, CharSequence)}.
     * <p>
     * The target is matched literally, and all occurrences are replaced.
     *
     * @param input       The input string.
     * @param target      The target string.
     * @param replacement The replacement string.
     * @return The replaced string.
     */
    @NotNull
    public static String replaceQuickly(@NotNull final String input,
                                        @NotNull final String target,
                                        @NotNull final String replacement) {
        int targetLength = target.length();

        // Count the number of original occurrences
        int count = 0;
        for (
                int index = input.indexOf(target);
                index != -1;
                index = input.indexOf(target, index + targetLength)
        ) {
            count++;
        }

        if (count == 0) {
            return input;
        }

        int replacementLength = replacement.length();
        int inputLength = input.length();

        // Pre-calculate the final size of the StringBuilder
        int newSize = inputLength + (replacementLength - targetLength) * count;
        StringBuilder result = new StringBuilder(newSize);

        int start = 0;
        for (
                int index = input.indexOf(target);
                index != -1;
                index = input.indexOf(target, start)
        ) {
            result.append(input, start, index);
            result.append(replacement);
            start = index + targetLength;
        }

        result.append(input, start, inputLength);
        return result.toString();
    }

    /**
     * Line wrap a list of strings while preserving formatting.
     *
     * @param input      The input list.
     * @param lineLength The approximate length of each line.
     * @return The wrapped lines.
     */
    @NotNull
    public static List<String> lineWrap(@NotNull final List<String> input,
                                        final int lineLength) {
        return lineWrap(input, lineLength, true);
    }

    /**
     * Line wrap a list of strings while preserving formatting.
     *
     * @param input          The input list.
     * @param lineLength     The approximate length of each line.
     * @param preserveMargin If the string has a margin, add it to the next line.
     * @return The wrapped lines.
     */
    @NotNull
    public static List<String> lineWrap(@NotNull final List<String> input,
                                        final int lineLength,
                                        final boolean preserveMargin) {
        return input.stream()
                .flatMap(line -> lineWrap(line, lineLength, preserveMargin).stream())
                .toList();
    }

    /**
     * Line wrap a string while preserving formatting.
     *
     * @param input      The input string.
     * @param lineLength The approximate length of each line.
     * @return The wrapped lines.
     */
    @NotNull
    public static List<String> lineWrap(@NotNull final String input,
                                        final int lineLength) {
        return lineWrap(input, lineLength, true);
    }

    /**
     * Line wrap a string while preserving formatting.
     * <p>
     * The string is broken at the first whitespace character once the line has exceeded the given
     * length, so lines are approximately, not exactly, that long. Colours and decorations carry
     * over across the break. The returned lines are legacy strings.
     *
     * @param input          The input string.
     * @param lineLength     The approximate length of each line.
     * @param preserveMargin If the string has a margin, add it to the start of each line.
     * @return The wrapped lines.
     */
    @NotNull
    public static List<String> lineWrap(@NotNull final String input,
                                        final int lineLength,
                                        final boolean preserveMargin) {
        int margin = preserveMargin ? getMargin(input) : 0;
        TextComponent space = Component.text(" ");

        Component asComponent = toComponent(input);

        // The component contains the text as its children, so the child components
        // are accessed like this:
        List<TextComponent> children = new ArrayList<>();

        if (asComponent instanceof TextComponent) {
            children.add((TextComponent) asComponent);
        }

        for (Component child : asComponent.children()) {
            children.add((TextComponent) child);
        }

        // Start by splitting the component into individual characters.
        List<TextComponent> letters = new ArrayList<>();
        for (TextComponent child : children) {
            for (char c : child.content().toCharArray()) {
                letters.add(Component.text(c).mergeStyle(child));
            }
        }

        List<Component> lines = new ArrayList<>();
        List<TextComponent> currentLine = new ArrayList<>();
        boolean isFirstLine = true;

        for (TextComponent letter : letters) {
            if (currentLine.size() > lineLength && letter.content().isBlank()) {
                lines.add(Component.join(JoinConfiguration.noSeparators(), currentLine));
                currentLine.clear();
                isFirstLine = false;
            } else {
                // Add margin if starting a new line.
                if (currentLine.isEmpty() && !isFirstLine) {
                    if (preserveMargin) {
                        for (int i = 0; i < margin; i++) {
                            currentLine.add(space);
                        }
                    }
                }

                currentLine.add(letter);
            }
        }

        // Push last line.
        lines.add(Component.join(JoinConfiguration.noSeparators(), currentLine));

        // Convert back to legacy strings.
        return lines.stream().map(StringUtils::toLegacy)
                .collect(Collectors.toList());
    }

    /**
     * Get a string's margin.
     * <p>
     * The margin is the index at which the trimmed content starts, i.e. the number of leading
     * whitespace characters.
     *
     * @param input The input string.
     * @return The margin.
     */
    public static int getMargin(@NotNull final String input) {
        return input.indexOf(input.trim());
    }

    /**
     * Convert a string to title case.
     * <p>
     * The string is split on single spaces; the first character of each word is upper-cased and
     * the rest of it lower-cased. The original spacing, including runs of consecutive spaces, is
     * preserved.
     *
     * @param string The string to convert.
     * @return The title-cased string.
     */
    @NotNull
    public static String toTitleCase(@NotNull final String string) {
        if (string.isEmpty()) {
            return string;
        }
        String[] words = string.split(" ", -1);
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    result.append(word.substring(1).toLowerCase());
                }
            }
            if (i < words.length - 1) {
                result.append(' ');
            }
        }
        return result.toString();
    }

    /**
     * Options for formatting.
     */
    public enum FormatOption {
        /**
         * Completely formatted: colour codes are translated and placeholders are resolved.
         */
        WITH_PLACEHOLDERS,

        /**
         * Completely formatted without placeholders: colour codes are translated, but placeholders
         * are left in the string untouched.
         */
        WITHOUT_PLACEHOLDERS
    }

    private StringUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
