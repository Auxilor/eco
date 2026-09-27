package com.willfp.eco.util;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

/**
 * Converts legacy formatting codes into MiniMessage tags.
 * <p>
 * Legacy colour codes reset every decoration, and a reset code clears everything, so the tags
 * this opens are closed again at those points. Tags that were already MiniMessage are passed
 * through untouched.
 */
final class LegacyToMiniMessage {
    /**
     * Legacy colour codes and their MiniMessage tags.
     */
    private static final Map<Character, String> COLORS = Map.ofEntries(
            Map.entry('0', "black"),
            Map.entry('1', "dark_blue"),
            Map.entry('2', "dark_green"),
            Map.entry('3', "dark_aqua"),
            Map.entry('4', "dark_red"),
            Map.entry('5', "dark_purple"),
            Map.entry('6', "gold"),
            Map.entry('7', "gray"),
            Map.entry('8', "dark_gray"),
            Map.entry('9', "blue"),
            Map.entry('a', "green"),
            Map.entry('b', "aqua"),
            Map.entry('c', "red"),
            Map.entry('d', "light_purple"),
            Map.entry('e', "yellow"),
            Map.entry('f', "white")
    );

    /**
     * Legacy decoration codes and their MiniMessage tags.
     */
    private static final Map<Character, String> DECORATIONS = Map.of(
            'k', "obfuscated",
            'l', "bold",
            'm', "strikethrough",
            'n', "underlined",
            'o', "italic"
    );

    /**
     * Sprite and player head tags.
     */
    private static final Pattern OBJECT_TAG_PATTERN = Pattern.compile(
            "<(sprite|head)(:[^>]*)?>",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * Convert legacy codes to MiniMessage tags.
     *
     * @param legacy The text with legacy codes.
     * @return The text with MiniMessage tags.
     */
    @NotNull
    static String convert(@NotNull final String legacy) {
        StringBuilder builder = new StringBuilder(legacy.length() + 16);
        Deque<String> open = new ArrayDeque<>();
        int index = 0;

        while (index < legacy.length()) {
            char character = legacy.charAt(index);

            if (character != '§' || index + 1 >= legacy.length()) {
                builder.append(character);
                index++;
                continue;
            }

            char code = Character.toLowerCase(legacy.charAt(index + 1));

            if (code == 'x' && isHexSequence(legacy, index)) {
                closeAll(builder, open);
                builder.append("<color:#");

                for (int offset = 3; offset <= 13; offset += 2) {
                    builder.append(Character.toLowerCase(legacy.charAt(index + offset)));
                }

                builder.append('>');
                open.push("color");
                index += 14;
            } else if (COLORS.containsKey(code)) {
                closeAll(builder, open);
                builder.append('<').append(COLORS.get(code)).append('>');
                open.push(COLORS.get(code));
                index += 2;
            } else if (DECORATIONS.containsKey(code)) {
                builder.append('<').append(DECORATIONS.get(code)).append('>');
                open.push(DECORATIONS.get(code));
                index += 2;
            } else if (code == 'r') {
                closeAll(builder, open);
                index += 2;
            } else {
                builder.append(character);
                index++;
            }
        }

        closeAll(builder, open);

        return builder.toString();
    }

    /**
     * Remove sprite and head tags, for servers that can't show them.
     *
     * @param message The MiniMessage text.
     * @return The text without object tags.
     */
    @NotNull
    static String stripObjectTags(@NotNull final String message) {
        return OBJECT_TAG_PATTERN.matcher(message).replaceAll("");
    }

    private static boolean isHexSequence(@NotNull final String legacy,
                                         final int start) {
        if (start + 14 > legacy.length()) {
            return false;
        }

        for (int offset = 2; offset < 14; offset += 2) {
            if (legacy.charAt(start + offset) != '§'
                    || Character.digit(legacy.charAt(start + offset + 1), 16) < 0) {
                return false;
            }
        }

        return true;
    }

    private static void closeAll(@NotNull final StringBuilder builder,
                                 @NotNull final Deque<String> open) {
        while (!open.isEmpty()) {
            builder.append("</").append(open.pop()).append('>');
        }
    }

    /**
     * Utility class, cannot be instantiated.
     */
    private LegacyToMiniMessage() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
