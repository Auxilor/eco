package com.willfp.eco.util;

import com.willfp.eco.util.integrations.placeholder.PlaceholderManager;
import com.willfp.eco.util.optional.Prerequisite;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static net.md_5.bungee.api.ChatColor.COLOR_CHAR;

@UtilityClass
public class StringUtils {
    /**
     * Regex for gradients.
     */
    private static final String GRADIENT_REGEX = "<\\$#[A-Fa-f0-9]{6}>";

    /**
     * Translate a string - converts Placeholders and Color codes.
     *
     * @param message The message to translate.
     * @param player  The player to translate placeholders with respect to.
     * @return The message, translated.
     */
    public String translate(@NotNull final String message,
                            @Nullable final Player player) {
        String processedMessage = message;
        if (Prerequisite.MINIMUM_1_16.isMet()) {
            processedMessage = translateGradients(processedMessage);
        }
        processedMessage = PlaceholderManager.translatePlaceholders(processedMessage, player);
        processedMessage = translateHexColorCodes(processedMessage);
        processedMessage = ChatColor.translateAlternateColorCodes('&', processedMessage);
        return processedMessage;
    }

    /**
     * Translate a string without respect to a player.
     *
     * @param message The message to translate.
     * @return The message, translated.
     * @see StringUtils#translate(String, Player)
     */
    public String translate(@NotNull final String message) {
        return translate(message, null);
    }

    private static String translateHexColorCodes(@NotNull final String message) {
        Pattern hexPattern = Pattern.compile("&#" + "([A-Fa-f0-9]{6})" + "");
        Matcher matcher = hexPattern.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                    + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                    + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                    + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5));
        }

        return matcher.appendTail(buffer).toString();
    }

    /**
     * Apply gradients to the provided string.
     *
     * @param message the string to parse.
     * @return the parsed string.
     */
    public static String translateGradients(@NotNull final String message) {
        List<String> hexes = new ArrayList<>();
        Matcher matcher = Pattern.compile(GRADIENT_REGEX).matcher(message);
        while (matcher.find()) {
            hexes.add(matcher.group().replace("<$", "").replace(">", ""));
        }
        int hexIndex = 0;
        List<String> texts = new LinkedList<>(Arrays.asList(message.split(GRADIENT_REGEX)));
        StringBuilder finalMsg = new StringBuilder();
        for (String text : texts) {
            if (texts.get(0).equalsIgnoreCase(text)) {
                finalMsg.append(text);
                continue;
            }
            if (text.length() == 0) {
                continue;
            }
            if (hexIndex + 1 >= hexes.size()) {
                if (!finalMsg.toString().contains(text)) {
                    finalMsg.append(text);
                }
                continue;
            }
            String fromHex = hexes.get(hexIndex);
            String toHex = hexes.get(hexIndex + 1);
            finalMsg.append(insertFades(text, fromHex, toHex));
            hexIndex++;
        }
        return finalMsg.toString();
    }

    private static String insertFades(@NotNull final String message,
                                      @NotNull final String fromHex,
                                      @NotNull final String toHex) {
        boolean bold = message.contains("&l");
        boolean italic = message.contains("&o");
        String msg = message;
        msg = msg.replace("&l", "");
        msg = msg.replace("&o", "");
        int length = msg.length();
        Color fromRGB = Color.decode(fromHex);
        Color toRGB = Color.decode(toHex);
        double rStep = Math.abs((double) (fromRGB.getRed() - toRGB.getRed()) / length);
        double gStep = Math.abs((double) (fromRGB.getGreen() - toRGB.getGreen()) / length);
        double bStep = Math.abs((double) (fromRGB.getBlue() - toRGB.getBlue()) / length);
        if (fromRGB.getRed() > toRGB.getRed()) {
            rStep = -rStep; //200, 100
        }
        if (fromRGB.getGreen() > toRGB.getGreen()) {
            gStep = -gStep; //200, 100
        }
        if (fromRGB.getBlue() > toRGB.getBlue()) {
            bStep = -bStep; //200, 100
        }
        Color finalColor = new Color(fromRGB.getRGB());
        msg = msg.replaceAll(GRADIENT_REGEX, "");
        msg = msg.replace("", "<$>");
        for (int index = 0; index <= length; index++) {
            int red = (int) Math.round(finalColor.getRed() + rStep);
            int green = (int) Math.round(finalColor.getGreen() + gStep);
            int blue = (int) Math.round(finalColor.getBlue() + bStep);
            if (red > 255) {
                red = 255;
            }
            if (red < 0) {
                red = 0;
            }
            if (green > 255) {
                green = 255;
            }
            if (green < 0) {
                green = 0;
            }
            if (blue > 255) {
                blue = 255;
            }
            if (blue < 0) {
                blue = 0;
            }
            finalColor = new Color(red, green, blue);
            String hex = "#" + Integer.toHexString(finalColor.getRGB()).substring(2);
            String formats = "";
            if (bold) {
                formats += ChatColor.BOLD;
            }
            if (italic) {
                formats += ChatColor.ITALIC;
            }
            msg = msg.replaceFirst("<\\$>", ChatColor.of(hex) + formats);
        }
        return msg;
    }

    /**
     * Internal implementation of {@link String#valueOf}.
     * Formats collections and doubles better.
     *
     * @param object The object to convert to string.
     * @return The object stringified.
     */
    public String internalToString(@Nullable final Object object) {
        if (object == null) {
            return "null";
        }

        if (object instanceof Integer) {
            return ((Integer) object).toString();
        } else if (object instanceof String) {
            return (String) object;
        } else if (object instanceof Double) {
            return NumberUtils.format((Double) object);
        } else if (object instanceof Collection<?>) {
            Collection<?> c = (Collection<?>) object;
            return c.stream().map(StringUtils::internalToString).collect(Collectors.joining(", "));
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
    public String removePrefix(@NotNull final String string,
                               @NotNull final String prefix) {
        if (string.startsWith(prefix)) {
            return string.substring(prefix.length());
        }
        return string;
    }
}
