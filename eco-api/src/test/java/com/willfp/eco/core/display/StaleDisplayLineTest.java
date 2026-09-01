package com.willfp.eco.core.display;

import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StaleDisplayLineTest {
    /**
     * The shape eco writes a display line in, which revert recognises.
     */
    private static Component ecoLine(final String text) {
        return Component.empty().append(
                Component.text(Display.PREFIX).append(Component.text(text, NamedTextColor.GRAY))
        );
    }

    /**
     * The same line after something has round-tripped the lore through legacy strings, which
     * revert doesn't recognise.
     */
    private static Component flattenedLine(final String text) {
        return Component.empty()
                .append(Component.text(Display.PREFIX))
                .append(Component.text(text, NamedTextColor.GRAY));
    }

    @Test
    public void testNothingIsRemovedWhenNoLineIsWrittenTwice() {
        List<Component> before = List.of(Component.text("Foreign lore"));
        List<Component> after = List.of(Component.text("Foreign lore"), ecoLine("Display line"));

        Assertions.assertNull(DisplayLines.withoutStaleLines(before, after));
    }

    @Test
    public void testStaleLineLeftBehindByRevertIsRemoved() {
        // The flattened copy survived revert, then the module appended it again.
        List<Component> before = List.of(flattenedLine("Display line"));
        List<Component> after = List.of(flattenedLine("Display line"), ecoLine("Display line"));

        List<Component> result = DisplayLines.withoutStaleLines(before, after);

        Assertions.assertEquals(List.of(ecoLine("Display line")), result);
    }

    @Test
    public void testEveryStaleLineOfAMultiLineItemIsRemoved() {
        List<Component> before = List.of(
                flattenedLine("----------------"),
                flattenedLine("Talent")
        );

        List<Component> after = List.of(
                flattenedLine("----------------"),
                flattenedLine("Talent"),
                ecoLine("----------------"),
                ecoLine("Talent")
        );

        List<Component> result = DisplayLines.withoutStaleLines(before, after);

        Assertions.assertEquals(List.of(ecoLine("----------------"), ecoLine("Talent")), result);
    }

    @Test
    public void testForeignLoreIsKeptWhenAStaleLineIsRemoved() {
        Component foreign = Component.text("Enchantment description");

        List<Component> before = List.of(foreign, flattenedLine("Display line"));
        List<Component> after = List.of(foreign, flattenedLine("Display line"), ecoLine("Display line"));

        List<Component> result = DisplayLines.withoutStaleLines(before, after);

        Assertions.assertEquals(List.of(foreign, ecoLine("Display line")), result);
    }

    @Test
    public void testOnlyAsManyCopiesAreRemovedAsWereWrittenAgain() {
        // Two copies were already there, the module wrote one, so one copy is stale.
        List<Component> before = List.of(flattenedLine("Line"), flattenedLine("Line"));
        List<Component> after = List.of(
                flattenedLine("Line"),
                flattenedLine("Line"),
                ecoLine("Line")
        );

        List<Component> result = DisplayLines.withoutStaleLines(before, after);

        Assertions.assertEquals(List.of(flattenedLine("Line"), ecoLine("Line")), result);
    }

    @Test
    public void testEmptyLoreIsLeftAlone() {
        Assertions.assertNull(DisplayLines.withoutStaleLines(List.of(), List.of(ecoLine("Line"))));
        Assertions.assertNull(DisplayLines.withoutStaleLines(List.of(ecoLine("Line")), List.of()));
    }
}
