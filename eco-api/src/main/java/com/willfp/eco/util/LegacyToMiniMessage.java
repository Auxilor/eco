package com.willfp.eco.util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyFormat;
import org.jetbrains.annotations.NotNull;

/**
 * Converts legacy formatting codes into MiniMessage tags.
 * <p>
 * Legacy colour codes reset every decoration, and a reset code clears everything, so the tags
 * this opens are closed again at those points. Tags that were already MiniMessage are passed
 * through untouched, and are reset at those points too, as legacy formatting always did. A
 * MiniMessage colour tag or closing tag resets the legacy formatting before it, for the same
 * reason.
 */
final class LegacyToMiniMessage {
    /**
     * MiniMessage tags that set a colour.
     */
    private static final Set<String> COLOR_TAGS = Set.of("color", "colour", "c", "gradient", "rainbow", "transition", "pride");

    /**
     * MiniMessage tags that never need closing.
     */
    private static final Set<String> SELF_CLOSING_TAGS = Set.of(
            "sprite", "head", "newline", "br", "selector", "sel", "score", "nbt", "data",
            "keybind", "key", "lang", "tr", "translate", "lang_or", "tr_or", "translate_or"
    );

    /**
     * Quoted MiniMessage tag arguments, which hold text of their own.
     */
    private static final Pattern QUOTED_ARGUMENT_PATTERN = Pattern.compile("'([^']*)'|\"([^\"]*)\"");

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
        return new Conversion(legacy).run();
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

    /**
     * A tag open at the current point.
     *
     * @param name The tag name.
     * @param user If the tag was written as MiniMessage, rather than opened for a legacy code.
     */
    private record OpenTag(@NotNull String name, boolean user) {
    }

    /**
     * One conversion of a string.
     */
    private static final class Conversion {
        /**
         * The text with legacy codes.
         */
        private final String legacy;

        /**
         * The converted text.
         */
        private final StringBuilder builder;

        /**
         * The open tags, innermost first.
         */
        private final Deque<OpenTag> open = new ArrayDeque<>();

        /**
         * MiniMessage tags that a legacy code reset, whose closing tags must be dropped.
         */
        private final List<String> resetUserTags = new ArrayList<>();

        /**
         * Create a conversion.
         *
         * @param legacy The text with legacy codes.
         */
        Conversion(@NotNull final String legacy) {
            this.legacy = legacy;
            this.builder = new StringBuilder(legacy.length() + 16);
        }

        /**
         * Run the conversion.
         *
         * @return The text with MiniMessage tags.
         */
        @NotNull
        String run() {
            int index = 0;

            while (index < this.legacy.length()) {
                char character = this.legacy.charAt(index);

                if (character == '<') {
                    int skipped = this.userTag(index);

                    if (skipped > 0) {
                        index += skipped;
                        continue;
                    }
                }

                if (character != '§' || index + 1 >= this.legacy.length()) {
                    this.builder.append(character);
                    index++;
                    continue;
                }

                char code = Character.toLowerCase(this.legacy.charAt(index + 1));
                LegacyFormat format = LegacyComponentSerializer.parseChar(code);

                if (code == 'x' && this.isHexSequence(index)) {
                    this.reset();
                    this.builder.append("<color:#");

                    for (int offset = 3; offset <= 13; offset += 2) {
                        this.builder.append(Character.toLowerCase(this.legacy.charAt(index + offset)));
                    }

                    this.builder.append('>');
                    this.open.push(new OpenTag("color", false));
                    index += 14;
                } else if (format == null) {
                    this.builder.append(character);
                    index++;
                } else if (format.color() instanceof NamedTextColor color) {
                    this.reset();
                    this.openLegacy(NamedTextColor.NAMES.key(color));
                    index += 2;
                } else if (format.decoration() != null) {
                    this.openLegacy(TextDecoration.NAMES.key(format.decoration()));
                    index += 2;
                } else {
                    this.reset();
                    index += 2;
                }
            }

            this.closeLegacy();

            return this.builder.toString();
        }

        private void openLegacy(@NotNull final String name) {
            this.builder.append('<').append(name).append('>');
            this.open.push(new OpenTag(name, false));
        }

        /**
         * Track and write a MiniMessage tag starting at an index. Legacy codes in its quoted
         * arguments are converted on their own. A closing tag resets the legacy formatting after
         * it, and is dropped if a legacy code already reset its tag.
         *
         * @param start The index of the opening bracket.
         * @return The number of characters this wrote or dropped, or 0 to copy the tag as text.
         */
        private int userTag(final int start) {
            int end = this.legacy.indexOf('>', start);

            if (end < 0 || (start > 0 && this.legacy.charAt(start - 1) == '\\')) {
                return 0;
            }

            String tag = this.legacy.substring(start + 1, end);
            boolean closing = tag.startsWith("/");
            String name = tag.substring(closing ? 1 : 0).split("[: ]", 2)[0].toLowerCase(Locale.ROOT);

            if (closing) {
                if (this.closeUser(name)) {
                    this.builder.append(this.legacy, start, end + 1);
                } else if (!this.resetUserTags.remove(name)) {
                    return 0;
                }

                this.closeLegacy();
                return end - start + 1;
            }

            if (name.equals("reset")) {
                this.open.clear();
                this.resetUserTags.clear();
            } else if (!tag.endsWith("/") && !SELF_CLOSING_TAGS.contains(name)) {
                if (isColorTag(name)) {
                    this.closeLegacy();
                }

                this.open.push(new OpenTag(name, true));
            }

            Matcher matcher = QUOTED_ARGUMENT_PATTERN.matcher(tag);
            this.builder.append('<');

            while (matcher.find()) {
                char quote = matcher.group().charAt(0);
                String argument = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
                matcher.appendReplacement(this.builder, Matcher.quoteReplacement(quote + convert(argument) + quote));
            }

            matcher.appendTail(this.builder);
            this.builder.append('>');

            return end - start + 1;
        }

        /**
         * Close an open MiniMessage tag, closing the legacy tags inside it first. The closing tag
         * itself is not written.
         *
         * @param name The tag name.
         * @return If the tag was open.
         */
        private boolean closeUser(@NotNull final String name) {
            if (!this.open.contains(new OpenTag(name, true))) {
                return false;
            }

            while (!this.open.isEmpty()) {
                OpenTag tag = this.open.pop();

                if (tag.user() && tag.name().equals(name)) {
                    return true;
                }

                if (!tag.user()) {
                    this.builder.append("</").append(tag.name()).append('>');
                }
            }

            return true;
        }

        /**
         * Close the legacy tags at the top of the stack, up to the first MiniMessage tag.
         */
        private void closeLegacy() {
            while (!this.open.isEmpty() && !this.open.peek().user()) {
                this.builder.append("</").append(this.open.pop().name()).append('>');
            }
        }

        /**
         * Reset all formatting, as a legacy colour or reset code does.
         */
        private void reset() {
            if (this.open.stream().noneMatch(OpenTag::user)) {
                this.closeLegacy();
                return;
            }

            this.builder.append("<reset>");

            for (OpenTag tag : this.open) {
                if (tag.user()) {
                    this.resetUserTags.add(tag.name());
                }
            }

            this.open.clear();
        }

        private boolean isHexSequence(final int start) {
            if (start + 14 > this.legacy.length()) {
                return false;
            }

            for (int offset = 2; offset < 14; offset += 2) {
                if (this.legacy.charAt(start + offset) != '§'
                        || Character.digit(this.legacy.charAt(start + offset + 1), 16) < 0) {
                    return false;
                }
            }

            return true;
        }

        private static boolean isColorTag(@NotNull final String name) {
            return name.startsWith("#") || COLOR_TAGS.contains(name) || NamedTextColor.NAMES.value(name) != null;
        }
    }

    /**
     * Utility class, cannot be instantiated.
     */
    private LegacyToMiniMessage() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
