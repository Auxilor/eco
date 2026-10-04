package com.willfp.eco.core.display;

import com.willfp.eco.util.StringUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DisplayMarkerTest {
    private static Component content() {
        return Component.text("Display line", NamedTextColor.GRAY);
    }

    @Test
    public void testMarkedLineWithPrefixIsADisplayLine() {
        Component line = DisplayLines.mark(content(), true);

        Assertions.assertTrue(DisplayLines.isDisplayLine(line));
        Assertions.assertEquals(Display.PREFIX + "§7Display line", StringUtils.toLegacy(line));
    }

    @Test
    public void testMarkedLineWithoutPrefixIsADisplayLine() {
        Component line = DisplayLines.mark(content(), false);

        Assertions.assertTrue(DisplayLines.isDisplayLine(line));
        Assertions.assertEquals("§7Display line", StringUtils.toLegacy(line));
    }

    @Test
    public void testMarkedLineInsideTheItalicWrapperIsADisplayLine() {
        Component line = Component.empty()
                .decoration(TextDecoration.ITALIC, false)
                .append(DisplayLines.mark(content(), false));

        Assertions.assertTrue(DisplayLines.isDisplayLine(line));
    }

    @Test
    public void testMarkerIsNotOnTheWrapper() {
        Component line = DisplayLines.mark(content(), true);

        Assertions.assertEquals(DisplayLines.MARKER, line.insertion());
        Assertions.assertNull(content().insertion());
    }

    @Test
    public void testPrefixIsRemovedFromTheOwningShape() {
        Component line = Component.empty().append(
                Component.text(Display.PREFIX).append(content())
        );

        Assertions.assertEquals("§7Display line", StringUtils.toLegacy(DisplayLines.withoutPrefix(line)));
    }

    @Test
    public void testPrefixIsRemovedFromTheFlatShape() {
        Component line = Component.empty()
                .append(Component.text(Display.PREFIX))
                .append(content());

        Assertions.assertFalse(DisplayLines.isDisplayLine(line));
        Assertions.assertEquals("§7Display line", StringUtils.toLegacy(DisplayLines.withoutPrefix(line)));
    }

    @Test
    public void testMarkerIsRemovedFromAMarkedLine() {
        Component stripped = DisplayLines.withoutPrefix(DisplayLines.mark(content(), true));

        Assertions.assertEquals("§7Display line", StringUtils.toLegacy(stripped));
        Assertions.assertFalse(DisplayLines.isDisplayLine(stripped));
    }
}
